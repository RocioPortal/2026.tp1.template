package com.bibliotech.model;

public record Socio(
        String dni,
        String nombre,
        String email,
        TipoSocio tipo
) {
    // Tipo de socio con su límite de préstamos
    public enum TipoSocio {
        ESTUDIANTE(3),
        DOCENTE(5);

        private final int limitePrestamos;

        // Constructor del Enum
        TipoSocio(int limite) {
            this.limitePrestamos = limite;
        }

        public int getLimitePrestamos() {
            return limitePrestamos;
        }
    }
}