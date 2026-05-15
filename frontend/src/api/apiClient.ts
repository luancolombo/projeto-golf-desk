const API_BASE_URL = "/api";
const AUTHORIZATION_HEADER = "Authorization";
const BEARER_PREFIX = "Bearer ";

let accessToken: string | null = null;
let refreshToken: string | null = null;
let refreshPromise: Promise<TokenRefreshResponse> | null = null;
let onTokenRefresh: ((response: TokenRefreshResponse) => void) | null = null;
let onRefreshFailed: (() => void) | null = null;

type RequestOptions = {
  body?: unknown;
  headers?: HeadersInit;
  signal?: AbortSignal;
};

type ErrorBody = {
  message?: string;
  error?: string;
};

type TokenRefreshResponse = {
  accessToken: string;
  refreshToken: string;
  tokenType: "Bearer";
  expiresIn: number;
};

export class ApiError extends Error {
  readonly status: number;
  readonly statusText: string;
  readonly body: unknown;

  constructor(message: string, status: number, statusText: string, body: unknown) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.statusText = statusText;
    this.body = body;
  }
}

function buildUrl(path: string) {
  return `${API_BASE_URL}${path.startsWith("/") ? path : `/${path}`}`;
}

function buildHeaders(hasBody: boolean, headers?: HeadersInit) {
  const requestHeaders = new Headers(headers);

  if (hasBody && !requestHeaders.has("Content-Type")) {
    requestHeaders.set("Content-Type", "application/json");
  }

  if (accessToken && !requestHeaders.has(AUTHORIZATION_HEADER)) {
    requestHeaders.set(AUTHORIZATION_HEADER, `${BEARER_PREFIX}${accessToken}`);
  }

  return requestHeaders;
}

function isErrorBody(value: unknown): value is ErrorBody {
  return typeof value === "object" && value !== null;
}

function shouldTryRefresh(path: string, response: Response, hasRetried: boolean) {
  const normalizedPath = path.startsWith("/") ? path : `/${path}`;
  return response.status === 401 && !hasRetried && !normalizedPath.startsWith("/auth/");
}

async function parseResponse(response: Response) {
  if (response.status === 204) {
    return undefined;
  }

  const contentType = response.headers.get("content-type") || "";

  if (contentType.includes("application/json")) {
    return response.json();
  }

  return response.text();
}

async function refreshAccessToken() {
  if (!refreshToken) {
    throw new ApiError("Sessao expirada.", 401, "Unauthorized", null);
  }

  if (!refreshPromise) {
    refreshPromise = fetch(buildUrl("/auth/refresh"), {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ refreshToken })
    })
      .then(async (response) => {
        const parsedBody = await parseResponse(response);

        if (!response.ok) {
          const fallbackMessage = "Nao foi possivel renovar a sessao.";
          const message = isErrorBody(parsedBody)
            ? parsedBody.message || parsedBody.error || fallbackMessage
            : String(parsedBody || fallbackMessage);

          throw new ApiError(message, response.status, response.statusText, parsedBody);
        }

        const tokenResponse = parsedBody as TokenRefreshResponse;
        setApiAuthTokens(tokenResponse.accessToken, tokenResponse.refreshToken);
        onTokenRefresh?.(tokenResponse);
        return tokenResponse;
      })
      .catch((error) => {
        clearApiAuthTokens();
        onRefreshFailed?.();
        throw error;
      })
      .finally(() => {
        refreshPromise = null;
      });
  }

  return refreshPromise;
}

async function request<T>(
  path: string,
  method: string,
  options: RequestOptions = {},
  hasRetried = false
): Promise<T> {
  const hasBody = options.body !== undefined;
  const response = await fetch(buildUrl(path), {
    method,
    headers: buildHeaders(hasBody, options.headers),
    body: hasBody ? JSON.stringify(options.body) : undefined,
    signal: options.signal
  });

  if (shouldTryRefresh(path, response, hasRetried)) {
    await refreshAccessToken();
    return request<T>(path, method, options, true);
  }

  const parsedBody = await parseResponse(response);

  if (!response.ok) {
    const fallbackMessage = "Nao foi possivel concluir a operacao.";
    const message = isErrorBody(parsedBody)
      ? parsedBody.message || parsedBody.error || fallbackMessage
      : String(parsedBody || fallbackMessage);

    throw new ApiError(message, response.status, response.statusText, parsedBody);
  }

  return parsedBody as T;
}

export function setApiAccessToken(token: string | null) {
  accessToken = token;
}

export function setApiAuthTokens(nextAccessToken: string | null, nextRefreshToken: string | null) {
  accessToken = nextAccessToken;
  refreshToken = nextRefreshToken;
}

export function clearApiAccessToken() {
  accessToken = null;
}

export function clearApiAuthTokens() {
  accessToken = null;
  refreshToken = null;
}

export function setApiTokenRefreshHandler(handler: ((response: TokenRefreshResponse) => void) | null) {
  onTokenRefresh = handler;
}

export function setApiRefreshFailureHandler(handler: (() => void) | null) {
  onRefreshFailed = handler;
}

export const apiClient = {
  setAuthTokens(nextAccessToken: string | null, nextRefreshToken: string | null) {
    setApiAuthTokens(nextAccessToken, nextRefreshToken);
  },
  setAccessToken(token: string | null) {
    setApiAccessToken(token);
  },
  clearAuthTokens() {
    clearApiAuthTokens();
  },
  clearAccessToken() {
    clearApiAccessToken();
  },
  get<T>(path: string, options?: Omit<RequestOptions, "body">) {
    return request<T>(path, "GET", options);
  },
  post<T, TBody = unknown>(path: string, body: TBody, options?: Omit<RequestOptions, "body">) {
    return request<T>(path, "POST", { ...options, body });
  },
  put<T, TBody = unknown>(path: string, body: TBody, options?: Omit<RequestOptions, "body">) {
    return request<T>(path, "PUT", { ...options, body });
  },
  delete(path: string, options?: Omit<RequestOptions, "body">) {
    return request<void>(path, "DELETE", options);
  }
};
