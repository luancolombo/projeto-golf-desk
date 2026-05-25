import type { ReactNode } from "react";
import { CalendarDays, CircleDollarSign, LogOut, Package, Search, UsersRound } from "lucide-react";

import { authService } from "@/api";
import type { AppPage } from "@/App";
import golfDeskLogo from "@/assets/logo_golf_api.png";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Separator } from "@/components/ui/separator";
import { useAuth } from "@/features/auth/AuthContext";

type AppShellProps = {
  activePage: AppPage;
  apiStatus: string;
  canAccessCashRegister: boolean;
  children: ReactNode;
  onNavigate: (page: AppPage) => void;
};

const navigationItems: Array<{
  icon: typeof UsersRound;
  label: string;
  page: AppPage;
}> = [
  { icon: UsersRound, label: "Players", page: "players" },
  { icon: CalendarDays, label: "Agenda", page: "agenda" },
  { icon: Package, label: "Materiais", page: "materials" },
  { icon: CircleDollarSign, label: "Caixa", page: "cash-register" },
];

const pageTitles: Record<AppPage, { eyebrow: string; title: string; description: string }> = {
  players: {
    eyebrow: "Cadastros",
    title: "Players",
    description: "Cadastro e consulta de jogadores conectados a API Spring Boot.",
  },
  agenda: {
    eyebrow: "Tee sheet",
    title: "Agenda",
    description: "Operacao diaria de tee times, bookings, check-ins, materiais e pagamentos.",
  },
  materials: {
    eyebrow: "Estoque",
    title: "Materiais",
    description: "Controle de estoque, precos, devolucoes e avarias dos materiais alugaveis.",
  },
  "cash-register": {
    eyebrow: "Financeiro",
    title: "Caixa",
    description: "Conferencia, fechamento e relatorio diario do caixa.",
  },
};

export function AppShell({
  activePage,
  apiStatus,
  canAccessCashRegister,
  children,
  onNavigate,
}: AppShellProps) {
  const { clearSession, refreshToken, user } = useAuth();
  const pageTitle = pageTitles[activePage];
  const visibleNavigationItems = navigationItems.filter(
    (item) => item.page !== "cash-register" || canAccessCashRegister,
  );

  async function handleLogout() {
    try {
      if (refreshToken) {
        await authService.logout(refreshToken);
      }
    } finally {
      clearSession();
    }
  }

  return (
    <div className="min-h-screen bg-[#f4f7fb] text-slate-950">
      <aside className="fixed inset-y-0 left-0 z-30 hidden w-72 flex-col bg-[#052d5f] text-white shadow-2xl lg:flex">
        <div className="px-8 py-10">
          <img
            alt="Golf Desk OS"
            className="h-auto w-52 rounded-md bg-white/95 p-2 shadow-lg"
            src={golfDeskLogo}
          />
        </div>

        <nav className="flex-1 space-y-1 px-4" aria-label="Navegacao principal">
          {visibleNavigationItems.map((item) => {
            const Icon = item.icon;
            const isActive = activePage === item.page;

            return (
              <button
                className={[
                  "flex h-14 w-full items-center gap-4 rounded-md px-5 text-left text-lg font-semibold transition-colors",
                  isActive ? "bg-[#335d8c] text-white shadow-inner" : "text-blue-50 hover:bg-white/10",
                ].join(" ")}
                key={item.page}
                type="button"
                onClick={() => onNavigate(item.page)}
              >
                <Icon className="h-5 w-5" />
                {item.label}
              </button>
            );
          })}
        </nav>

        <div className="px-8 py-8">
          <div className="rounded-md border border-white/10 bg-white/10 p-4">
            <span className="text-xs uppercase tracking-[0.2em] text-blue-100">Sessao</span>
            <strong className="mt-2 block truncate">{user?.name || "Usuario"}</strong>
            <span className="text-sm text-blue-100">{user?.role}</span>
          </div>
        </div>
      </aside>

      <div className="lg:pl-72">
        <header className="sticky top-0 z-20 border-b border-slate-200 bg-[#f4f7fb]/95 px-5 py-5 backdrop-blur lg:px-10">
          <div className="flex flex-col gap-4 xl:flex-row xl:items-center xl:justify-between">
            <nav className="flex gap-2 overflow-x-auto pb-1 lg:hidden" aria-label="Navegacao principal compacta">
              {visibleNavigationItems.map((item) => {
                const Icon = item.icon;
                const isActive = activePage === item.page;

                return (
                  <Button
                    className={[
                      "h-10 shrink-0 border-slate-200",
                      isActive ? "bg-[#052d5f] text-white hover:bg-[#052d5f]" : "bg-white text-slate-700 hover:bg-slate-100",
                    ].join(" ")}
                    key={item.page}
                    type="button"
                    variant="outline"
                    onClick={() => onNavigate(item.page)}
                  >
                    <Icon className="h-4 w-4" />
                    {item.label}
                  </Button>
                );
              })}
            </nav>

            <div className="flex min-w-0 flex-1 items-center gap-4">
              <div className="min-w-[180px] rounded-md border border-slate-200 bg-white px-4 py-3 shadow-sm">
                <span className="block text-xs font-semibold uppercase tracking-[0.18em] text-slate-500">API</span>
                <strong className="text-sm text-slate-900">{apiStatus}</strong>
              </div>
              <div className="relative hidden max-w-xs flex-1 md:block">
                <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
                <Input
                  className="h-11 border-slate-200 bg-white pl-10 text-slate-900 placeholder:text-slate-400"
                  placeholder="Search"
                  type="search"
                />
              </div>
            </div>

            <div className="flex items-center gap-3">
              <div className="rounded-md border border-slate-200 bg-white px-4 py-3 text-right shadow-sm">
                <span className="block text-xs text-slate-500">Logado como</span>
                <strong className="block max-w-[220px] truncate text-sm text-slate-950">{user?.name || "Usuario"}</strong>
                <Badge className="mt-2 bg-emerald-100 text-emerald-800 hover:bg-emerald-100">{user?.role}</Badge>
              </div>
              <Button className="h-11 bg-slate-900 text-white hover:bg-slate-800" type="button" onClick={handleLogout}>
                <LogOut className="h-4 w-4" />
                Sair
              </Button>
            </div>
          </div>
        </header>

        <main className="px-5 py-8 lg:px-10">
          <div className="mb-8">
            <p className="mb-2 text-xs font-black uppercase tracking-[0.22em] text-[#052d5f]">{pageTitle.eyebrow}</p>
            <h1 className="text-4xl font-black tracking-normal text-slate-950 md:text-5xl">{pageTitle.title}</h1>
            <p className="mt-3 max-w-3xl text-base leading-7 text-slate-600">{pageTitle.description}</p>
          </div>
          <Separator className="mb-8 bg-slate-200" />
          {children}
        </main>
      </div>
    </div>
  );
}
