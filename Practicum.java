package com.company.main;

import java.util.HashMap;

public class Practicum {
    // Ключи для map
    private static final String V = "verbose";
    private static final String N = "number";
    private static final String T = "text";
    private static final String B = "big-number";
    private static final String COMMAND_NAME = "program";
    private static final int MINIMAL_SYMBOLS_IN_FULL_PARAM = 7;
    private static final int NUM_OF_REQUIRED_PARAMS = 3;
    private static final int NUM_OF_NOT_REQUIRED_PARAMS = 1;

    public static void main(String[] args) {
        // Test cases
        String[] args1 = {"program", "-n", "23", "-t", "text", "--text=text2", "-b", "123454322", "--verbose"}; // Валидная строка
        String[] args2 = {"program", "--text=text2", "-b", "123454322", "--verbose"}; // Не валидная строка
        String[] args3 = {"Unknown command", "-n", "123", "-b", "3123123123", "-t", "text1231", "-v"}; // Не валидная строка
        String[] args4 = {"program", "--number=123", "123", "-b", "3123123123", "-t", "text1231", "-v"}; // Валидная строка
        String[] args5 = {"program", "--number=123", "123", "--big-number=12343234", "3123123123", "-t", "text1231", "-v"}; // Валидная строка
        String[] args6 = {"program", "--number=123", "1234", "--big-number=12343234", "3123123123", "--text=text", "text1231"}; // Валидная строка
        String[] args7 = {"program", "--number=123", "123", "--bigErrorNumber=12343234", "3123123123", "--text", "text1231"}; // Не валидная строка
        String[] args8 = {"program", "--number=123", "123", "--big-number=12343234", "3123123123", "--big-number=0", "-t"}; // Не валидная строка
        String[] args9 = {"program", "--number=123", "123", "--big-number=12343234", "3123123123", "--big-number=0", "-t", "1234"}; // Валидная строка
        String[] args10 = {}; // Не валидная строка
        System.out.println(parseArgs(args10));
    }

    /**
     * Парсер одной CLI команды.
     *
     * @param args набор входных аргументов в CLI.
     * @return возвращает map, c извлеченными значениями параметров.
     * key(String) - полное имя параметра.
     * value(String) - значение параметра.
     */
    public static HashMap<String, String> parseArgs(String[] args) {
        final int size = args.length;
        if (size < NUM_OF_REQUIRED_PARAMS + 1) {
            System.out.println("Input error.");
            return null;
        }
        if (!args[0].equals(COMMAND_NAME)) {
            System.out.println("Unknown command.");
            return null;
        }
        final HashMap<String, String> map = new HashMap<>(NUM_OF_REQUIRED_PARAMS + NUM_OF_NOT_REQUIRED_PARAMS);
        boolean flagForAllInput = true;
        // Массив отображающий, какие параметры уже были добавлены. true - добавлен. false - не добавлен. Необязательные параметры в конце массива!
        boolean[] wasPut = new boolean[NUM_OF_REQUIRED_PARAMS + NUM_OF_NOT_REQUIRED_PARAMS]; // 0 - T, 1 - N, 2 - B, 3 - V
        int elemSize;
        map.put(V, "false");
        map.put(T, "");
        map.put(N, "");
        map.put(B, "");
        for (int i = 1; i < size; i++) {
            elemSize = args[i].length() - 1;
            switch (args[i]) { // Проверка по неполным параметрам
                case "-v":
                case "--verbose": {
                    if (!wasPut[3]) {
                        wasPut[3] = true;
                        map.put(V, "true");
                    }
                    break;
                }
                case "-n": {
                    if (i + 1 < size && !wasPut[1]) {
                        if (isNumber(args[i + 1])) {
                            map.put(N, args[i + 1]);
                            wasPut[1] = true;
                            i++;
                        }
                    }
                    break;
                }
                case "-t": {
                    if (i + 1 < size && !wasPut[0]) {
                        map.put(T, args[i + 1]);
                        wasPut[0] = true;
                        i++;
                    }
                    break;
                }
                case "-b": {
                    if (i + 1 < size && !wasPut[2]) {
                        if (isNumber(args[i + 1])) {
                            map.put(B, args[i + 1]);
                            wasPut[2] = true;
                            i++;
                        }
                    }
                    break;
                }
            }
            if (elemSize >= MINIMAL_SYMBOLS_IN_FULL_PARAM) {
                checkFullParamsName(args[i], map, wasPut);
            }
            for (boolean b : wasPut) { // Проверка на введеность всех  параметров
                if (!b) { // Если один из обязательных не введен, то выходим и помечаем это
                    flagForAllInput = false;
                    break;
                }
            }
            if (flagForAllInput) { // Если условие из цикла ниразу не выполнилось, флаг останется true, значит все обязательные параметры были добавлены
                flagForAllInput = false;
                break;
            }
            flagForAllInput = true; // Возвращаем значение флажку
        }
        if (flagForAllInput && map.containsValue("")) { // Если введены не все обязательные параметры, вывод ошибки.
            System.out.println("Not at all required params input");
            return null;
        }
        if (map.containsValue("")) { // Если значение параметра не было подано
            System.out.println("Not at all required params value input");
            return null;
        }
        return map;
    }

    /**
     * Проверяет, содержит ли строка только символы цифр.
     *
     * @param s строка.
     * @return true - Если в строке только цифры, false - Если есть другие символы.
     */
    private static boolean isNumber(String s) {
        final int size = s.length();
        for (int i = 0; i < size; i++) {
            if (s.charAt(i) < '0' || s.charAt(i) > '9') {
                System.out.println(1);
                return false;
            }
        }
        return true;
    }

    /**
     * Парсер параметров с полным именем.
     *
     * @param s      Параметр с полным именем.
     * @param map    Ссылка на instance HashMap.
     * @param wasPut Проверка на добавления значения параметра в map, до этого. true - был добавлен, false - не был добавлен.
     */
    private static void checkFullParamsName(String s, HashMap<String, String> map, boolean[] wasPut) {
        final int size = s.length();
        StringBuilder addValueToMap = new StringBuilder();
        if (size + 1 < 7) { // Проверка на минимальную длину полного параметра
            return;
        }
        if (s.charAt(0) != '-' || s.charAt(1) != '-') { // Проверка на содержание двух '-' перед полным именем параметра
            return;
        }
        for (int i = 2; i < size; i++) { // Считывание имени параметра
            if (s.charAt(i) == '=') {
                break;
            }
            addValueToMap.append(s.charAt(i));
        }
        switch (addValueToMap.toString()) { // В зависимости от имени добаляем в нужную секцию map, если еще не было добавлено.
            case V:
                return;
            case N: {
                if (!wasPut[1]) {
                    addValueToMap = new StringBuilder();
                    wasPut[1] = true;
                    for (int i = 9; i < size; i++) { // i = 9 - количество символов до значения параметра.
                        addValueToMap.append(s.charAt(i));
                    }
                    if (isNumber(addValueToMap.toString())) {
                        map.put(N, addValueToMap.toString());
                    }
                }
                return;
            }
            case B: {
                if (!wasPut[2]) {
                    addValueToMap = new StringBuilder();
                    wasPut[2] = true;
                    for (int i = 13; i < size; i++) {
                        addValueToMap.append(s.charAt(i));
                    }
                    if (isNumber(addValueToMap.toString())) {
                        map.put(B, addValueToMap.toString());
                    }
                }
                return;
            }
            case T: {
                if (!wasPut[0]) {
                    addValueToMap = new StringBuilder();
                    wasPut[0] = true;
                    for (int i = 7; i < size; i++) {
                        addValueToMap.append(s.charAt(i));
                    }
                    map.put(T, addValueToMap.toString());
                }
                return;
            }
        }
    }
}