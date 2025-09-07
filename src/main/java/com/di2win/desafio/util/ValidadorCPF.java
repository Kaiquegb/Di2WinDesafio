package com.di2win.desafio.util;

public class ValidadorCPF {
    public static boolean isValido(String cpf) {
        if (cpf == null) return false;
        String digitos = cpf.replaceAll("\\D", "");
        if (digitos.length() != 11) return false;
        if (digitos.chars().distinct().count() == 1) return false; // 000... / 111...

        int primeiro = calculaDigito(digitos.substring(0, 9), 10);
        int segundo  = calculaDigito(digitos.substring(0,10), 11);
        return digitos.charAt(9) == (char) (primeiro + '0')
                && digitos.charAt(10) == (char) (segundo + '0');
    }

    private static int calculaDigito(String base, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < base.length(); i++) {
            soma += Character.getNumericValue(base.charAt(i)) * (pesoInicial - i);
        }
        int resto = soma % 11;
        return (resto < 2) ? 0 : 11 - resto;
    }

    public static String somenteDigitos(String cpf) {
        return cpf == null ? null : cpf.replaceAll("\\D", "");
    }
}
