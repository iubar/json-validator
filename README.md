[![CircleCI](https://dl.circleci.com/status-badge/img/gh/iubar/json-validator/tree/master.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/iubar/json-validator/tree/master)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/30c97be1b9c34500b37f295fe8bc6d9f)](https://www.codacy.com/gh/iubar/json-validator/dashboard)

# Json Cli Validator
A tool to check one or more json files against its json schema

## How to use it

````java
    java -jar json-validator-jar-with-dependencies.jar <schema-file> (<json folder>|<json file>)
````

## References
- [Introducing JSON](http://www.json.org/json-it.html)
- [Json-Schema.org](https://json-schema.org) [Understanding JSON Schema](https://json-schema.org/understanding-json-schema/index.html)
- [Tour](- https://tour.json-schema.org/)
- [Specifications](http://json-schema.org/specification-links.html) 
- [Docs](https://json-schema.org/understanding-json-schema/)
- [Implementations](http://json-schema.org/implementations.html) 
- [Projects list](https://json-schema.org/tools)
- [Specifiche](https://json-schema.org/specification-links.html) (!)

### Json validator - Implementations
- [Everit](https://github.com/everit-org/json-schema)  (currently in maintenance mode and superseded by erosb/json-sKema)
- [Justify](https://github.com/leadpony/justify) (the default strategy for that project)
- [Networknt](https://github.com/networknt/json-schema-validator)
- [json-sKema](https://github.com/erosb/json-sKema) (It implements [the draft 2020-12 specification](https://json-schema.org/draft/2020-12))
- [json-schema-validator](https://github.com/java-json-tools/json-schema-validator) (not used)
- [snowy-json](https://github.com/ssilverman/snowy-json#using-snow-in-your-own-projects) (not used)

### Json schema genertor
- https://github.com/victools/jsonschema-generator (not used)

### Tools
- [JSONschema.Net (online schema generator)](https://jsonschema.net)
- [JSONschemaValidator.Net (online validator)](https://www.jsonschemavalidator.net)
- [JsonFormatter.org](https://jsonformatter.org/json-editor)
- [JsonEditorOnline.org](https://jsoneditoronline.org)

### Json libraries

- [A look at JSR 374 - Java API for JSON Processing (JSON-P)](https://golb.hplar.ch/2019/08/json-p.html)
- [jackson vs gson](https://www.baeldung.com/jackson-vs-gson)

### JSON-B and JSON-P

JSON-B is just a layer on top of JSON-P: it does the binding part, while it delegates all the raw JSON processing to JSON-P. 
You can mix and match any JSON-B implementation with any JSON-P implementation.

### Pay attention

* See the new possibility offers by the Json schema version 2019-09: https://stackoverflow.com/questions/62957704/can-additionalproperties-apply-to-nested-objects-in-json-schema
(currently the default version adopted by this project is the "draft-07")

## How to generate a schema from a class

* see https://github.com/mbknor/mbknor-jackson-jsonSchema
 

 
## Logging
 
### Perché anche log.info() a volte non si vede

1. Il problema: due livelli da superare
Un messaggio viene visualizzato solo se passa entrambi i filtri:

````
Logger.fine("msg")
       │
       ▼
  [Filtro 1] Logger.level  → default: INFO   ← BLOCCATO qui se < INFO
       │
       ▼
  [Filtro 2] Handler.level → default: INFO   ← BLOCCATO qui se < INFO
       │
       ▼
    Output
````
    
Anche i messaggi INFO (che superano entrambi i filtri) possono sparire per altri motivi:
2. Maven Surefire esegue i test in un processo separato (forked JVM)

- Lo stdout/stderr del processo figlio può essere bufferizzato o rediretto
- Per vederlo aggiungi nel pom.xml:

````xml
<plugin>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <useFile>false</useFile>      <!-- non redirige output su file -->
        <trimStackTrace>false</trimStackTrace>
    </configuration>
</plugin>
````


3. Il ConsoleHandler scrive su System.err, non System.out

- Alcuni IDE e tool mostrano stderr in modo diverso (in rosso, o in un tab separato)
- Potresti starlo cercando nel posto sbagliato


### La soluzione minima senza file di configurazione

````java
java@BeforeAll
static void setupLogging() {
    // Sblocca il root logger
    Logger root = Logger.getLogger("");
    root.setLevel(Level.ALL);

    // Sblocca anche l'handler
    for (Handler h : root.getHandlers()) {
        h.setLevel(Level.ALL);
    }
}
````

### La soluzione

1) File logging.properties (configurazione esterna)
Crea il file in src/test/resources/logging.properties


Poi dì alla JVM di usarlo, nel pom.xml:

````xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <argLine>
            -Djava.util.logging.config.file=src/test/resources/logging.properties
        </argLine>
    </configuration>
</plugin>
````

2) Configurazione programmatica (nessun file esterno)
Usando una JUnit 5 Extension dedicata, configurata una volta sola:

````java
// src/test/java/com/tuopackage/LoggingExtension.java
public class LoggingExtension implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.ALL);

        // Rimuovi handler di default (evita duplicati)
        for (Handler h : rootLogger.getHandlers()) {
            rootLogger.removeHandler(h);
        }

        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        handler.setFormatter(new SimpleFormatter());
        rootLogger.addHandler(handler);

        // Formato leggibile
        System.setProperty(
            "java.util.logging.SimpleFormatter.format",
            "%1$tF %1$tT [%4$s] %2$s - %5$s%6$s%n"
        );
    }
}
````

Poi applicala alle classi di test:

````java
java@ExtendWith(LoggingExtension.class)
class MyServiceTest {

    private static final Logger log =
        Logger.getLogger(MyServiceTest.class.getName());

    @Test
    void testCalcolo() {
        log.info("Inizio test");
        log.fine("Dettaglio debug");
        log.warning("Attenzione!");

        int result = 2 + 2;
        log.info("Risultato: " + result);

        assertEquals(4, result);
    }
}
````

### TestReporter

TestReporter è un'interfaccia JUnit 5 che permette di pubblicare dati nel report ufficiale del test, non semplicemente su console. È iniettata automaticamente da JUnit come parametro del metodo di test.


Esempio base

````java
javaclass MyServiceTest {

    @Test
    void testConReporter(TestReporter reporter) {

        reporter.publishEntry("fase", "inizio calcolo");

        int result = 6 * 7;

        reporter.publishEntry("risultato", String.valueOf(result));
        reporter.publishEntry("test completato");

        assertEquals(42, result);
    }
}
````

Puoi usarlo in @BeforeEach / @AfterEach

````java
@BeforeEach
void setup(TestReporter reporter, TestInfo info) {
    reporter.publishEntry("avvio test", info.getDisplayName());
}
````

Il vantaggio principale è che le entry finiscono nel file surefire-reports/*.xml generato da Maven, leggibile da strumenti CI come Jenkins o GitHub Actions


### fail()

fail() serve a forzare il fallimento di un test, non a loggare messaggi.
In Eclipse, il messaggio passato a fail() è la prima riga del Failure Trace
Se vuoi un messaggio visibile durante l'esecuzione, fail() non è lo strumento giusto per quello scopo. Combinalo con un log.

````java
@Test
void myTest(TestReporter reporter) {
    reporter.publishEntry("stato", "punto critico raggiunto"); // oppure usa qui il logger
    fail("qualcosa è andato storto");
}
````


Il punto debole: perdi lo stack trace originale
fail() lancia una nuova AssertionFailedError, sostituendo l'eccezione originale. Nel Failure Trace di Eclipse non vedi più dove è scoppiata l'eccezione.

La soluzione è passare l'eccezione originale come causa:

````java
catch (Exception e) {
    log.severe("Errore inatteso: " + e.getMessage());
    fail("Test fallito: " + e.getMessage(), e);  // ← overload con Throwable cause
}
````

L'importante è usare sempre fail(msg, e) invece di fail(msg) per non perdere informazioni.

