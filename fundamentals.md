## Fundamentals

### First program
Ctrl-1 magic button
Ctrl-Shift-L	keys
saját template...
src.zip
@formatter:off (be kell állítani)
Ctrl-M tab max
Project/Clean	ha lecsúszik a bin az src-től
PackageExplorer: Filter, összekapcsolás
Ctrl-Shift-R	resources
Ctrl-Shift-T	all referenced classes
Ctrl-7		kicommentel
Ctrl-Shift-M	extract method

#### Debug support:
breakpoint
Ctrl-Shift-I	Inspect - részutasítások
Ctrl-3		Display - Inspect: magát az
objektumot lehet elemezni. attribútumokat
(Quick access rákeres mindenféle view-ra)
debug közben lehet javítani - mentésre újrafordítja
megállítani valahanyadik, vagy valamilyen esetnél egy ciklust:
conditional breakpoint (breakpoint -> jobb gomb -> properties)
látni futó process-eket is


objektum hierarchia föltérképezése:
Type Hierarchy View
Open Call Hierarchy - végigmegy hívási láncon
'toString', 'equals', 'hashCode' methods - 
Source-ból vagy Ctrl-1, vagy Ctrl-Space
push up, push down 	super vagy alosztályba nyom
Ctrl-U		örökölt függvények és változók is

# class 1
autoboxing 127 felett
hashcode - hashmap, ilyenek, binarysearch
array objectként viselkedik: lehet null, constructorja is van, 
az Object methodj
protected - utánanézni
strictfp - float. native - op.rendszerben értem
this -t kiadni constructorból nem szabad
super() jön először, az ős
az ősosztály konstruktora minkdenképp le kell fusson
default constructor ()
átbesszéltük: 2/15 -ig

# class 2
accessor - kontroll, getternél másolat, belső struktúrát el lehet rejteni
`finalize()` nem biztos, hogy lefut, nem tudjuk mikor
Interface többszörös öröklődés: meg kell adni, melyiket használjuk
annotation: plusz információ annak, aki ezt használni akarja. Fordítót kényszeríti:
hogy az ősosztályban is léteznie kell ennek a methodnak

* compilernek
* byte-code-ba belekerül: konfiguráció..
* futás időben is elérhető: milyen adatbázistáblban legyen tárolva, utána programot írni, ami megnézi

runtimeException / checkedException
programozási hibákra jó a runtime
recovarable exception-re checkedException

buffered - egyben tud flössölni

NIO.2 - val lehet aszinkron működést,
egyszerre több stream-en is tud dolgozni akár egy szál

Generics:
* futásidőben jönnyít
* objectként viselkednek

kérdezni:
* Console
* resources

eclipse.ini - memória, jvm, etc
string literalok, címek
PropertyResourceBundle - aktuálisat tölti be, megfelelő nyelvűt
így transzparens lesz az alkalmazás
MessageFormat - kimenet formázás

syserr - nem lehet konfigurálni fontosságot, és összekeveredik az outtal, nem lehet fájlba
logging framework-ök káosza
**SLF4J**
*Facade*: csak interface, nem kell ismerni logging FrameWorköt

# BRIDGE
logging átnézése!
* appenderek

...-over-slf4j.jar
ugyanolyan classok, mint a frameWork-ben, de továbbítja kéréseket az
SLF4J-nek

konzolon be lehessen írni dátumot és tippeket
allPercentages method nevét igésíteni, mit csinál
totoServicenek úgy, hogy ne legyen állapota
állapotmentes valami
Tototercice service = new TotoService();
List<Rounds> = service.parseCSV();
maxPrize = service.calculate(rounds)
myPrize = calculatePrizeForRound(rounds, date, outcome)
másik service, aminek az a dolga, hogy kommunikáljon a felhasználóval, TotoService-ba, vagy App mainbe
IOExceptionnel kell csinálni valamit

