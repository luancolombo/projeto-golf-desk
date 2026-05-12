const API_BASE_URL = "/api";

type RequestOptions = {
  body?: unknown;
  headers?: HeadersInit;
  signal?: AbortSignal;
};

type ErrorBody = {
  message?: string;
  error?: string;
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

function isErrorBody(value: unknown): value is ErrorBody {
  return typeof value === "object" && value !== null;
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

async function request<T>(path: string, method: string, options: RequestOptions = {}): Promise<T> {
  const hasBody = options.body !== undefined;
  const response = await fetch(buildUrl(path), {
    method,
    headers: {
      ...(hasBody ? { "Content-Type": "application/json" } : {}),
      ...options.headers
    },
    body: hasBody ? JSON.stringify(options.body) : undefined,
    signal: options.signal
  });

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

export const apiClient = {
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
