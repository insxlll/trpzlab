package com.example.appserver;

import javax.tools.*;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JavaSyntaxChecker extends SyntaxChecker {

    private boolean useCompilerAPI = true;

    public JavaSyntaxChecker() {
        this(true);
    }

    public JavaSyntaxChecker(boolean useCompilerAPI) {
        this.useCompilerAPI = useCompilerAPI;
    }

    @Override
    protected void preprocessCode() {
        
        code = code.replaceAll("//.*?(\r?\n|$)", "$1");
        
        
        Pattern multilineCommentPattern = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
        Matcher matcher = multilineCommentPattern.matcher(code);
        code = matcher.replaceAll("");
    }

    @Override
    protected void checkSpecificSyntax() {
        if (useCompilerAPI) {
            checkWithCompilerAPI();
        } else {
            checkWithRegexRules();
        }
    }


    private void checkWithCompilerAPI() {
        try {
            
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            
            if (compiler == null) {
                errors.add("ПОМИЛКА: Java компілятор недоступний. " +
                          "Переконайтеся, що використовується JDK, а не JRE");
                
                checkWithRegexRules();
                return;
            }

            
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            
            
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(
                diagnostics, 
                Locale.getDefault(), 
                null
            );

            
            String className = extractClassName(code);
            if (className == null) {
                className = "TempClass";
            }

            
            JavaFileObject file = new JavaSourceFromString(className, code);

            
            Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
            JavaCompiler.CompilationTask task = compiler.getTask(
                null,           
                fileManager,    
                diagnostics,    
                null,           
                null,           
                compilationUnits 
            );

            
            Boolean success = task.call();

            
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                String severity = getSeverityString(diagnostic.getKind());
                String message = String.format(
                    "[%s] Рядок %d, позиція %d: %s",
                    severity,
                    diagnostic.getLineNumber(),
                    diagnostic.getColumnNumber(),
                    diagnostic.getMessage(Locale.getDefault())
                );
                errors.add(message);
            }

            
            

            fileManager.close();

        } catch (IOException e) {
            errors.add("ПОМИЛКА: Не вдалося виконати перевірку синтаксису: " + e.getMessage());
        } catch (Exception e) {
            errors.add("ПОМИЛКА: Виняток при перевірці синтаксису: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void checkWithRegexRules() {
        checkClassDeclaration();
        checkMethodDeclarations();
        checkSemicolons();
        checkKeywords();
        checkStringLiterals();
    }


    private String extractClassName(String code) {
        Pattern pattern = Pattern.compile("\\b(?:public\\s+)?class\\s+([A-Za-z_][A-Za-z0-9_]*)");
        Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String getSeverityString(Diagnostic.Kind kind) {
        switch (kind) {
            case ERROR:
                return "ПОМИЛКА";
            case WARNING:
            case MANDATORY_WARNING:
                return "ПОПЕРЕДЖЕННЯ";
            case NOTE:
                return "ІНФО";
            case OTHER:
            default:
                return "ІНШЕ";
        }
    }

    

    private void checkClassDeclaration() {
        Pattern classPattern = Pattern.compile("\\b(?:public\\s+)?class\\s+([A-Za-z_][A-Za-z0-9_]*)");
        Matcher matcher = classPattern.matcher(code);
        
        if (!matcher.find()) {
            errors.add("ПОПЕРЕДЖЕННЯ: Не знайдено оголошення класу");
        } else {
            String className = matcher.group(1);
            if (!Character.isUpperCase(className.charAt(0))) {
                errors.add("ПОПЕРЕДЖЕННЯ: Ім'я класу '" + className + 
                          "' повинно починатися з великої літери (Java конвенція)");
            }
        }
    }

    private void checkMethodDeclarations() {
        Pattern methodPattern = Pattern.compile(
            "(?:public|private|protected)?\\s*(?:static)?\\s*\\w+\\s+\\w+\\s*\\([^)]*\\)\\s*\\{"
        );
        
        Matcher matcher = methodPattern.matcher(code);
        int methodCount = 0;
        while (matcher.find()) {
            methodCount++;
        }
        
        if (methodCount == 0 && code.contains("class")) {
            errors.add("ПОПЕРЕДЖЕННЯ: Клас не містить методів");
        }
    }

    private void checkSemicolons() {
        String[] lines = code.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            
            
            if (line.isEmpty() || line.endsWith("{") || line.endsWith("}") || 
                line.startsWith("//") || line.startsWith("/*") || line.startsWith("*")) {
                continue;
            }
            
            
            if ((line.contains("=") || line.startsWith("return") || 
                 line.matches(".*\\w+\\s*\\(.*\\).*")) && 
                !line.endsWith(";") && !line.endsWith("{") && !line.endsWith("}") &&
                !line.contains("class") && !line.contains("interface") && 
                !line.contains("for") && !line.contains("while") && !line.contains("if")) {
                errors.add("ПОПЕРЕДЖЕННЯ: Рядок " + (i + 1) + 
                          ": можливо пропущена крапка з комою");
            }
        }
    }

    private void checkKeywords() {
        String[] keywords = {"if", "else", "while", "for", "switch"};
        
        for (String keyword : keywords) {
            Pattern pattern = Pattern.compile("\\b" + keyword + "\\b(?!\\s*\\()");
            Matcher matcher = pattern.matcher(code);
            
            while (matcher.find()) {
                int pos = matcher.start();
                String after = code.substring(pos).trim();
                if (after.startsWith(keyword) && 
                    !after.substring(keyword.length()).trim().startsWith("(") &&
                    !after.substring(keyword.length()).trim().startsWith("{")) {
                    errors.add("ПОПЕРЕДЖЕННЯ: Ключове слово '" + keyword + 
                              "' повинно містити умову в дужках");
                }
            }
        }
    }

    private void checkStringLiterals() {
        int quoteCount = 0;
        boolean escaped = false;
        
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            
            if (c == '\\' && !escaped) {
                escaped = true;
                continue;
            }
            
            if (c == '"' && !escaped) {
                quoteCount++;
            }
            
            escaped = false;
        }
        
        if (quoteCount % 2 != 0) {
            errors.add("ПОМИЛКА: Незакритий рядковий літерал (непарна кількість лапок)");
        }
    }

    @Override
    protected void postProcessErrors() {
        
        errors = errors.stream()
                .distinct()
                .sorted((e1, e2) -> {
                    
                    if (e1.contains("ПОМИЛКА") && !e2.contains("ПОМИЛКА")) return -1;
                    if (!e1.contains("ПОМИЛКА") && e2.contains("ПОМИЛКА")) return 1;
                    if (e1.contains("ПОПЕРЕДЖЕННЯ") && e2.contains("ІНФО")) return -1;
                    if (e1.contains("ІНФО") && e2.contains("ПОПЕРЕДЖЕННЯ")) return 1;
                    return e1.compareTo(e2);
                })
                .collect(java.util.stream.Collectors.toList());
    }


    private static class JavaSourceFromString extends SimpleJavaFileObject {
        private final String code;

        public JavaSourceFromString(String name, String code) {
            super(
                URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension),
                Kind.SOURCE
            );
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }
}