import { ApiError } from "./apiClient";

const DEFAULT_ERROR_MESSAGE = "Nao foi possivel concluir a operacao.";

export function getApiErrorMessage(error: unknown, options?: { login?: boolean }) {
  if (error instanceof ApiError) {
    if (options?.login && error.status === 401) {
      return "Email ou senha invalidos.";
    }

    if (error.status === 401) {
      return "Sessao expirada. Faca login novamente para continuar.";
    }

    if (error.status === 403) {
      return "Acesso negado. Seu usuario nao tem permissao para esta acao.";
    }

    if (error.status === 409) {
      return error.message || "A operacao viola uma regra de negocio.";
    }

    if (error.status >= 500) {
      return "Erro interno da API. Tente novamente ou verifique os logs do backend.";
    }

    return error.message || DEFAULT_ERROR_MESSAGE;
  }

  if (error instanceof Error) {
    return error.message;
  }

  return DEFAULT_ERROR_MESSAGE;
}

export function getApiErrorResponse(error: unknown) {
  if (error instanceof ApiError) {
    return {
      status: error.status,
      statusText: error.statusText,
      message: getApiErrorMessage(error),
      body: error.body ?? { message: error.message }
    };
  }

  return { error: getApiErrorMessage(error) };
}
