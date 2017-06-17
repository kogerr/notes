# Design Patterns

* elegancia: egyszerű megoldás bonyolult problémára
* adaptív: ne legyen nehéz egy-egy módosítást bevezetni
* ne legyen redundáns: rejtett dependencia ne legyen

- megoldás hasonló problémákra
- mindig kicsit máshogy: nem szeretjük az ismétlést

mire jó?
- közös nyelv a programozók között
- jó szokások elsajátítása, OOP
- gyorsul a fejlesztés
- elkerülni tévutakat/csapdákat

## OOP
delegálni: másik objektummal megoldatni

## Prerequisits
- először statikus inicializációs blokkok futnak le, dinamikus, konstruktor
- ősnek és alsztálynak is lefut a konstruktora

- az anonymus class igaából `extends`
- privát konsruktorú ősosztályt nem lehet extendelni, mert alosztály nem hívja
- volatile fielddel oldotta meg vagy synchronized accessorokkal
  - synchronized kulcsszó láthatósági garanciát is ad

## Creational Patterns
- elfedni, hogy milyen konkrét objektumokat fogunk létrehozni (melyik osztály)
- elfedni objektum belső felépítését, egyszerűsíteni
- objektumok vagy osztályok közötti kapcsolatokat akarjuk kihasználni

Motivation
- absztrakcióktól függés - interface, osztály helyett
- new kulcsszótól és konkrét oszálytól függés megszűntetése
- növeli a rugalmasságotiensnek nem kell ezzel törődnie

### Builder
elkülőníteni az objektum felépítését attól, hogy hogyan van reprezentálva
- pl különböző konvertálások, több fajta bemenet földolgozása
- külön osztály egy osztály létrehozására
- benne létrehozó és lekérdező method
- pl közös builder interface alá két különböző builder
- director pl fájl kiolvasására

#### Implementáció
- jó használni, ha sok-lépésű létrehozási folyamat van
- lehet üresenhagyni a nem kellő methodokat
- szokás láncolni builder methodokat
- lehet a builder adatgyűjtő osztály, amit megkap constructor-ba a classt
- lehet vonatszerűen fűzni építéskor
- ha sok a paraméter constructor-ban

- ha elválasztanánk egy összetettebb algoritmust, amire
- ha különböző bemenet lehet / több féle termék születhet
- el elehet nevezni a konstruktor paramétereket
- lehet validálni a paramétereket

Javában pl:
- StringBuilder
- StringBuffer
- ByteArrayOutputStream
- Calendar.Builder

### Factory Method
létrehozásra interface/method, subclassok eldöntik, mit példányosítanak
- másnéven: Virtual Constructor (virtuális = subclass fölülírja supert)

#### Implementáció
- alfaj:
  - abstract factory
  - factory method contains default implementation
- paraméterezett is lehet
- neve: newInstance, create...
- jellemzői:
  - létrehoz egy objektumot
  - visszatérési típusa absztakt osztály vagy interface
  - mert majd implementálják/fölülírják a subclassok

##### Static Factory Method:
- nem leszármazottakra bízzuk, hanem static
- constructor: private/protected
- nem muszály mindig újat létrehozni, visszadhat olyat, amit más használ, mert
immutable (+)
- visszaadhatja saját object-jét vagy altípusát (+)
- van neve (+)
- lehet kliens, de inkább gyártó oldalon

#### Java Példák
__Factory Methods:__
- Object.toString()
- Iterable.iterator()
  - interface-t ad vissza
  - leszármazott osztályokon a felelősség, hogy hogyan megy végig a kollekción
- ThreadFactory.newThread
  - új objektumot ad vissza...

__Static Factory:__
- Calendar.getInstance() - előnye(?) leszármazottal tér vissza (Gregórián)
- NumberFormat.getInstance() - ugyanúgy leszármazottal
- Charset.forName() - nem kell mindig új példányt létrehozni. először keres

### Singleton
egy osztályból egyetlen példány, globális hozzáféréssel
- pl egy alkalmazás főoldola, vagy bállítások, statisztika gyűjtés
- viszont nehéz belőle kijutni

#### Implementation
- private static field - _instatnce_
- private static method - _getInstance()_
- private constructor - hogy ne tudjanak több példányt

- 2 kategória:
  - Eager loading: egyből betöltődik
  - Lazy loading: on first usage

- Concurrent access: mindenki ezt az egyet fogja használni
- nem nagyon lehet subclass
- teljesítmény

#### Java Examples
- java.lang.Runtime()
  - egy JVMen belül egy van
  - static constructor -> Eager loading
- java.awt.GraphicsEnvironment() 
  - lazy loading:
```java
if (localEnv == null) {
	localEnv = createGE();
}
return localEnv;
```
  - synchronized hozza létre, hogy ne jöhessen létre egyszerre többször

#### Variants
- double check - hogy ne kelljen mindig szinkronizálni
- static inner singleton: lazy, de JVMre bízza, hogy csak egy lehet

### Other Vreational Patterns
- Abstract Factory
- Prototype: másolással bővítsünk - deepCopy / shallow copy

## Structural Patterns
kisebb objektumokat egy egységbe, felelősség szétosztása, flexibilis legyen

### Adapter
kényszerhelyzetet old meg, pl konnektor
- adott interface és implementáció, nem kompatibilisek
- interface alá behúzza az implementációt
- már megírt kód felhasználása
- Listenerek is: pl `ButtonListener` és `action` method közé
- _wrapper_ mert burkolóként veszi körül az implementációs osztályt
- Object Adapter: adapter tartalmazza az adaptálandó osztályt (composition)
  - megvalósítja az interface-t
  - kliens csak az interface-t látja
  - tipikusan delegációval
- Class Adapter: mintha többszörös öröklés. javaban csak interface
- szereplők:
  - Target: interface used by the client
  - client: collaborates with objects conforming to the Target interface
  - Adaptee: has incompatible interface
  - Adapter: adapts the interface of Adaptee to the target interface

#### Java Examples
- `Arays.asList(...)` - target: `List`, adaptee: `Array`
- `InputStreamReader(InputStream)` - target: `Reader`, adaptee: `InputStream`
- `RunnableAdapter` - target: `Callable`, adaptee: `Runnable`
- `MouseAdapter`
- `HashSet` - target: `Set`, adaptee: `HashMap`
- Guava: `Iterators.forEnumeration(Enumeration<T>)`

### Bridge
absztrakció elválasztása az implementációtól
- interface-ben vagy ősosztályba adjuk meg az absztrakciót, azt implementáljuk
- kompozíciós kapcsolat - futás időben is eldönthetjük, melyik implementáció
- továbbítja a kéréseket, plusz esetleg kényelmi metódusok: méret, stb
- implementation: 
  - egy vagy több
  - megfelelő implementáció eldőlhet futásidőben vagy config fileban
  - meg is lehet osztani az implementáló objektumokat
- jó:
  - elkerülni az öröklés hátrányait (kompozíció)
  - ha impl. változhat futásidőben
  - ha absztakció és implementáció függetlenül változik
  - ha meg akarjuk osztani az implementáló objektumot
- **Java Example**: `java.sql.DriverManager`

### Decorator
plusz felelősségek/műveletek hozzáadása dinamikusan
- örökléssel semmiképp nem lehet, mert statikus
- itt is **kompozíció**val
- pl: scrollozható panelben elhalyezni valamit - csak amikor kell
- pl: stramek egymásba ágyazása (kiírás elé csomagolások dekorálják)
- egy közös interface vagy absztrakt ősosztály alá pakolja az adott komponenst,
meg a dekorátort, ami plusz felelősséget ad
- továbbítja a kérést, a paramétereket átalakítja
- Interface conformance: dekorátor és konkrét komponens közös interface-re
- mikor jó?
  - felelősséget dinamikusan hozzáadni
  - elvenni
  - ha az örökléssel bővítés nem praktikus

#### Java Examples
- `ObjectInputStream` `BufferedInputStream` `GZIPInputStram` `FileInputStream`
- `SynchronizedCollection` - új felelősség: szinkronizálja az összes methodot
- `SynchronizedSet` - örököl a `SynchronizedCollection` dekorátortól!


### Facade
egyszerűsített egységesített hozzáférés bonyolult alrendszerhez
- kliensnél összevissza lenne
- legtöbb APInak van, általában ami alatta van sokkal bonyolultabb
- sokszor rétegzet architektúrában, rétegek közötti kommunikációra
- kliensnek nem kell belenyúlni alrendszer oszályaiba (**sportsbetting**)
- ismernie kell alrendszer osztályait (nem fedi fel belső struktúrát)
- delegálja kliens kéréseit
- alrendszer implementál, facade-ben nincsen üzleti logika
- csökken a kötés alrendszer és kliens között általában
- rétegzett architektúrában valszeg kell
- __Java Examples__: SLF4J, JOptionPane
- sokszor csak egyszerű method hívások vannak benne

### Proxy
Egy helyettesen keresztül használják a kliensek az objektumunkat
- pl ha hálózaton keresztül kommunikálunk, reference counting...
- helyettesítőnek és obektumnak azonos legyen az interface-e vagy ősosztálya
- csak továbbítja a kérést vagy beavatkozik és nem enged tovább vmit
- Remote proxy - távoli eljáráshívás
- Virtuális proxy: nem akarjuk, hogy mindig rendelkezésre álljon
- Protection proxy - nem engedünk át hívásokat
- __Java Examples__: `java.lang.reflect.Proxy`, `Java RMI` (távoli)

### Other
- Composite: rész egész viszonyt képvisel
  - pl XML DOM: egy elembe sok más elemet be lehet ágyazni rekurzív jelleggel
- Flyweight: optimalizálásra: több kliens is használhatja az objektumokat

## Behavioural Patterns
hívások lefutása egy bonyolultabb struktúrában
- változáson a hangsúly
- feladata elosztani osztályok között a felelősséget
- cél, hogy laza legyen a coupling - flexibilis legyen
- kompozíció inkább öröklés helyett

### Command
- kérés nem csak metódushívás pillanatnyi helyzete lehet, tehetjük objektumba
- így lehet pl időzíteni, sorrendezni (prioritás)
- használják visszalépés műveletének támogatásához
- Participants:
  - Client - látrehozza
  - Invoker - eltárolja és időzíti
  - Command - interface (nem kötelező)
  - ConcreteCommand - pl `execute` methoddal
  - Reciever - elszenvedi
- jó: időzítésre, undo, logging commands (to reapply them)
- __Java Examples__: `Runnable` átadja szálnak, `Callable` hívás eredményét..
- Example: Accounting... blockingQueue...

### Iterator
hozzáférés egy kollekció elemeihez, de ne fedjük fel a belső szekezetét
- felelőssége tudni, hol tartunk a végigjárásban

#### Implementation:
- external (amit megszoktunk) / internal (kap eseménykezelőt)
- általában nem támogatják a concurrent műveleteket, kivéve `remove()`
- hozzáférése van a kollekció adattagjaihoz - általában belső osztály

### Mediator
sok-sok objektum kommunikál, hálós lesz: mediator lebonyolítja a kommunikációt
- központi elemként elvégzi ezt a működést
- GUI-nál sokszor használják
- mediator minden kommunnikáló felet ismer, oda-vissza kapcsolat
- lehet mediator abstract class is
- jó
  - jól-definiált objektumcsoport akar kommunkálni
  - nem tudunk újrafelhasználni egy objektumot a kommunikáció miatt
  - ha egy viselkedés el van osztva sok osztály között.. nehéz a referencia
- exercise: Smart Home

### Observer
many-to-many kapcsolatot valósít meg objektumok között
- bizonyos objektum jelez, erre több objektum iratkozik fel
- Observer interface-en keresztül szól a Subject, eltárolja megfigyelők listáját

#### Implementation
- egy eseménykezelő több eseményre is feliratkozhat és majd megkülönbözteti
- kiválthatja az update-et:
  - egy másik observer
  - belső állapotváltozás
- értesíteni a változás után kell, h lekérdezhető legyen
- jó:
  - mediátor alternatívája
  - ha jól definiálható eseményeket tudunk meghatározni
  - ha egy változás több más objektumot értesít
- __Java Examples__: `Observer`, `Observable`

### Strategy
különböző algoritmusokat tudjunk definiálni és váltogatni
- pl Layout Manager - elhelyezi a komponenseket
- definiálni kell egy közös interface-t az algoritmusoknak
- kompozíció: változtathatunk futásidőben
- Participants:
  - Strategy: interface az összes algoritmusnak
  - ConctreteStrategyN: implements the algorithm using the Strategy interface
  - Context: 
    - amiben ezek az algoritmusok futnak
    - ahonnan az algoritmus le tudja kérdezni az adatokat (nem constructorben)
- Strategy és Context szorosan együttdologzik, hogy implementálják algoritmust
- van, hogy van alapértelmezett stratégia
- jó:
  - ha nagyon hasonló osztályaink vannak, kicsit térnek el (elkerülni ismétlést)
  - ha egy osztály több fajta viselkedést tud produkálni
- __Java Examples__: `Comparator` - sort strategy, `awt.image.BufferStrategy`

### Temaplate Method
egy vázat ad egy algoritmusnak és hagyjuk a leszármazottakra, hogyan töltik ki
- a Strategy alternatívája, de öröklést használunk composition helyett
- úgy tervezzük meg az ősosztályt, hogy subclassok tudják bővíteni
- tipikusan APIkban - pl servlet, android activity
- naming conventions: final és abstract methodokra figyeljünk
- jó:
  - ha vannak invariáns részei egy algoritmusnak, amik ugyanúgy maradnak
  - ha biztos, hogy ősosztály nem változik (hátránya, hogy öröklést használ)
  - elkerülni kódismétlést
- __Java Examples__:
  - `javax.servlet.http.HttpServlet` - kiírás implementálva van, többi abstract
  - `OutputStream`
- exercise: PackageFiller
