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



