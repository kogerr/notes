# Logging

- `System.out` & `System.err` - nem lehet megkülönböztetni a szintjét,
felhasználása, átirányítása nehézhes
- __Log4J__ 1999
  - banknak kellett egy eszköz, amivel követni tudták a rendszer állapotát
- __Java Util Logging (JUL)__ 2002, Java 1.4
  - faék egyszerű logging framework
  - semmivel nem kompatibilis
  - bugos, konfigurációval nem működik pl, nem tud több file-ba logolni
  - akkor jó, ha nem akarsz új dependency-t bevezetni, és elég minimáls
- __Apache Commons Logging (ACL)__ 2002
  - első _facade_
  - interface-t konfiguráltál, és be lehetett állitani milyen logging framework
- __Simple Logging Facade for Java (SLF4J)__ 2005 Ceki Gülcü
  - logger interface (megvalósítás nincs benne)
  - tudsz különböző loggerek közt variálni (JUL, LOG4J, van valami saját, null...)
  - ha jön egy új logging framework, azt alá lehet csapni, anélkül, hogy a kód változna
- __LogBack__ 2006, Ceki Gülcü
  - xml alapon konfigurálható logging framework
  - lehet dinamikusan változtatni a config-ot

## JUL
- log szintek
- konkrét logger implementáció
- ha jön egy log üenet: létrejön a háttérben egy `logRecord` object, ezt adja _handler_-eknek
- `Handler` kiírja vagy valami (ez egy absztrakt osztály)
- lehet csinálni saját handlert is, override-olni a method-okat
- `.property` file-okban kell állítani
- be lehet állítani egy szintet, ami alatti logüzenetekkel nem foglalkozik
- `formatter`t, abből is lehet sajátot

### GoHome példa
```java
import java.util.logging.Logger;
// ...
private static final Logger LOGGER = Logger.getLogger(GoHome.class.getName()); // a class nevét adjuk neki
// ...
LOGGER.severe("Start!");
```

- konfigurálni: `logging.properties` fájlt tenni a `resources`ba
  - ezt megadni: `java.util.logging.configFile`
  - ez összekeveredhet a `jre/lib` könyvtár azonos nevű file-jával
  - `FileHandler.pattern =` mi legyen a file
  - Filehandler `.limit`, `.count`, `level`, `.formatter`
- megadni ezt a file-t: `-Djava.util.logging.configFile=`


## Log4j
- szintén hierarchia van, de más a szintek neve
- ez már levadássza a class file-ból, milyen néven menjen a logger
- appanderek vannak: `ConsoleAppender`, `FileAppender`,
`NullAppender` - ha teljesítmény romlást jelent és kikapcsolod
- `log4j.properties`


## SLF4J
- az első _facade_ amit tudunk használni
- ebből közvetlenül csak az API JARt kell használni
- minden más konnektorokkal kapcsolódik
- megkeresi dinamikusan a classpath-ban az első loggert
- `NOPLoggerFactory` ha nincs semmi implementálva, nem loggol semmit

## LogBack
- slf4j-jel együtt szokás
- XML-ben lehet ezt is konfigurálni
- be van állítva a _root logger_,
ezen kívül lehet még az egyes loggereket konfigurálni
- figyelni kell, hogy ne legyen mellékhatása a logolásnak!
- performance-t is veszthetsz loggolással

### Külső logger beállítása példa
- letölti a JARokat
- `slf4j.simple`t és `slf4j-api`t teszi először `lib` mappába
```java
LOGGER = LoggerFactory.getLogger(GoHome.class);
// ...
LOGGER.error("error");
```
- `logback.xml` fájlt keres, az elsőt használja, amit talál
- a simple-t ki kell cserélni a logback classicra és logback core-ra

