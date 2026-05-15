import { type FormEvent, useState } from "react";
import { authService, getApiErrorMessage } from "../api";
import { AUTH_SESSION_MESSAGE_STORAGE_KEY, useAuth } from "../features/auth/AuthContext";

type LoginFeedback = {
  message: string;
  type: "success" | "error" | "";
};

function getInitialFeedback(): LoginFeedback {
  const sessionMessage = sessionStorage.getItem(AUTH_SESSION_MESSAGE_STORAGE_KEY);

  if (sessionMessage) {
    sessionStorage.removeItem(AUTH_SESSION_MESSAGE_STORAGE_KEY);
    return {
      message: sessionMessage,
      type: "error"
    };
  }

  return {
    message: "Informe suas credenciais para acessar o sistema.",
    type: ""
  };
}

export function LoginPage() {
  const { setSessionFromLogin } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [feedback, setFeedback] = useState<LoginFeedback>(() => getInitialFeedback());
  const [isLoading, setIsLoading] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setIsLoading(true);
    setFeedback({ message: "Autenticando usuario...", type: "success" });

    try {
      const response = await authService.login({
        email: email.trim(),
        password
      });

      setSessionFromLogin(response);
      setFeedback({ message: "Login realizado com sucesso.", type: "success" });
      setPassword("");
    } catch (error) {
      setFeedback({ message: getApiErrorMessage(error, { login: true }), type: "error" });
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <main className="app-shell login-shell">
      <section className="login-layout">
        <header className="login-intro">
          <p className="eyebrow">Golf Office</p>
          <h1>Acesso</h1>
          <p className="page-description">
            Entre com seu usuario para acessar a operacao do campo, agenda, materiais e caixa.
          </p>
        </header>

        <article className="panel login-panel">
          <div className="panel-header">
            <div>
              <p className="section-tag">Login</p>
              <h2>Sessao segura</h2>
            </div>
          </div>

          <form className="player-form" onSubmit={handleSubmit}>
            <label>
              <span>Email</span>
              <input
                autoComplete="username"
                required
                type="email"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
              />
            </label>

            <label>
              <span>Senha</span>
              <input
                autoComplete="current-password"
                required
                type="password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
              />
            </label>

            <p className={`feedback ${feedback.type}`.trim()}>{feedback.message}</p>

            <button className="primary-button" disabled={isLoading} type="submit">
              {isLoading ? "Entrando..." : "Entrar"}
            </button>
          </form>

          <div className="login-seed-note">
            <span>Usuario de desenvolvimento</span>
            <strong>manager@golfoffice.dev</strong>
            <p>A senha de teste esta documentada no README e nao fica preenchida no formulario.</p>
          </div>
        </article>
      </section>
    </main>
  );
}
