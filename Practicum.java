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
        String[] args8 = {"program", "-number=123", "123", "--big-number=12343234", "3123123123", "--big-number=0", "--text=text", "-n"}; // Не валидная строка
        String[] args9 = {"program", "--number=123", "123", "--big-number=12343234", "3123123123", "--big-number=0", "-t", "1234"}; // Валидная строка
        String[] args10 = {}; // Не валидная строка
        String[] args11 = {"program", "--text=text", "--big-number=123", "--number=1"}; // Валидная строка
        System.out.println(parseArgs(args8));
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
        if (size == 0 || !args[0].equals(COMMAND_NAME)) {
            throw new IllegalCallerException("Unknown command.");
        }
        final HashMap<String, String> map = new HashMap<>(NUM_OF_REQUIRED_PARAMS + NUM_OF_NOT_REQUIRED_PARAMS);
        String[] keys = {T, N, B, V}; //Ключи к необязательным параметрам в конце массива.
        for (int i = 0; i < NUM_OF_REQUIRED_PARAMS + NUM_OF_NOT_REQUIRED_PARAMS; i++) { // Заполнение Map пустыми строками.
            map.put(keys[i], "");
        }
        for (int i = 1; i < size; i++) {
            switch (args[i]) { // Проверка по неполным параметрам
                case "-v":
                case "--verbose": {
                    if (map.get(V).isEmpty()) {
                        map.put(V, "true");
                    }
                    break;
                }
                case "-n": {
                    if (i + 1 < size && map.get(N).isEmpty() && isNumber(args[i + 1])) {
                        map.put(N, args[i + 1]);
                        i++;
                    }
                    break;
                }
                case "-t": {
                    if (i + 1 < size && map.get(T).isEmpty()) {
                        map.put(T, args[i + 1]);
                        i++;
                    }
                    break;
                }
                case "-b": {
                    if (i + 1 < size && map.get(B).isEmpty() && isNumber(args[i + 1])) {
                        map.put(B, args[i + 1]);
                        i++;
                    }
                    break;
                }
            }
            if (args[i].length() > MINIMAL_SYMBOLS_IN_FULL_PARAM) {
                checkFullParamsName(args[i], map);
            }
            if (!map.containsValue("")) {
                break;
            }
        }
        for (int i = 0; i < NUM_OF_REQUIRED_PARAMS; i++) { // Проверка состояния Map после считывания всех параметров.
            if (map.get(keys[i]).isEmpty()) {
                throw new IllegalArgumentException("Required parameter not found -> --" + keys[i]);
            }
        }
        // Заполнение дефолтными значениями необязательных параметров, если они не были указаны.
        for (int i = NUM_OF_REQUIRED_PARAMS; i < NUM_OF_REQUIRED_PARAMS + NUM_OF_NOT_REQUIRED_PARAMS; i++) {
            if (map.get(keys[i]).isEmpty()) {
                map.put(keys[i], "false");
            }
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
                return false;
            }
        }
        return true;
    }

    /**
     * Парсер параметров с полным именем.
     *
     * @param s   Параметр с полным именем.
     * @param map Ссылка на instance HashMap.
     */
    private static void checkFullParamsName(String s, HashMap<String, String> map) {
        final int size = s.length();
        int valueStartIndex = size;
        if (s.charAt(0) != '-' || s.charAt(1) != '-') { // Проверка на содержание двух '-' перед полным именем параметра.
            return;
        }
        StringBuilder addValueToMap = new StringBuilder();
        for (int i = 2; i < size; i++) { // Считывание имени параметра.
            if (s.charAt(i) == '=') {
                valueStartIndex = i + 1; // Следующий символ после "=".
                break;
            }
            addValueToMap.append(s.charAt(i));
        }
        switch (addValueToMap.toString()) { // В зависимости от имени добаляем в нужную секцию map, если еще не было добавлено.
            case V:
                return;
            case N: {
                if (map.get(N).isEmpty()) {
                    addValueToMap = new StringBuilder();
                    for (int i = valueStartIndex; i < size; i++) {
                        addValueToMap.append(s.charAt(i));
                    }
                    if (isNumber(addValueToMap.toString())) {
                        map.put(N, addValueToMap.toString());
                    }
                }
                return;
            }
            case B: {
                if (map.get(B).isEmpty()) {
                    addValueToMap = new StringBuilder();
                    for (int i = valueStartIndex; i < size; i++) {
                        addValueToMap.append(s.charAt(i));
                    }
                    if (isNumber(addValueToMap.toString())) {
                        map.put(B, addValueToMap.toString());
                    }
                }
                return;
            }
            case T: {
                if (map.get(T).isEmpty()) {
                    addValueToMap = new StringBuilder();
                    for (int i = valueStartIndex; i < size; i++) {
                        addValueToMap.append(s.charAt(i));
                    }
                    map.put(T, addValueToMap.toString());
                }
                return;
            }
        }
    }
}