import type { CashRegisterClosureItem } from "../types/cashRegister";
import { formatDateTime, formatMoney, isPaymentMovement, itemTypeLabel, signedMoneyClass } from "./cashRegisterFormat";
import { Badge } from "../../../components/ui/badge";

type CashRegisterItemsTableProps = {
  items: CashRegisterClosureItem[];
};

export function CashRegisterItemsTable({ items }: CashRegisterItemsTableProps) {
  const paymentItems = items.filter(isPaymentMovement);

  return (
    <div className="overflow-x-auto rounded-lg border border-slate-200">
      <table>
        <thead className="bg-slate-50">
          <tr>
            <th>Tipo</th>
            <th>Referencia</th>
            <th>Descricao</th>
            <th>Metodo</th>
            <th>Data</th>
            <th>Valor</th>
          </tr>
        </thead>
        <tbody>
          {paymentItems.length === 0 ? (
            <tr>
              <td className="empty-state" colSpan={6}>
                Nenhum pagamento encontrado para este caixa.
              </td>
            </tr>
          ) : (
            paymentItems.map((item, index) => (
              <tr key={`${item.type}-${item.referenceId || index}-${item.occurredAt || index}`}>
                <td>
                  <Badge className="bg-slate-100 text-slate-700 hover:bg-slate-100">
                    {itemTypeLabel(item.type)}
                  </Badge>
                </td>
                <td>
                  <div className="row-main">{item.referenceCode || "-"}</div>
                  <div className="row-sub">ID #{item.referenceId || "-"}</div>
                </td>
                <td>{item.description || "-"}</td>
                <td>
                  <div className="row-main">{item.paymentMethod || "-"}</div>
                  <div className="row-sub">{item.paymentStatus || ""}</div>
                </td>
                <td>{formatDateTime(item.occurredAt)}</td>
                <td className={`money-cell ${signedMoneyClass(item.amount)}`.trim()}>{formatMoney(item.amount)}</td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}
