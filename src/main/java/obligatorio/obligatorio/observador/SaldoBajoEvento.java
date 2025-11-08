package obligatorio.obligatorio.observador;

import java.math.BigDecimal;

public class SaldoBajoEvento {
    private final BigDecimal saldoActual;

    public SaldoBajoEvento(BigDecimal saldoActual) {
        this.saldoActual = saldoActual;
    }

    public BigDecimal getSaldoActual() {
        return saldoActual;
    }
}
