# Refactoring
### introduction
~:
- easier to understand
- cheaper to modify
- no change in observable behaviour

why?
- improve the design of the software
- olvashatóbb legyen
- könnyebb bugokat megtalálni
- gyorsabban lehessen fejleszteni
- ne legyen duplikáció

van, hogy nehéz:
- nagy adatbázis modelleket
- publikált interface-eket
- architektúrális változások (pl biztonságos legyen)

### Bad Smells in Code
- Duplicate Code - struktúra ismétlődése is
- hosszú methodok
- nagy osztály (felelősségek száma)
- paraméter lista: 3 még belefér
- divergent/szerteágazó módosítás: pl felhasználói igényre - enum switch
- shotgun surgery (sörétes): egy kis módosítást sok helyen lekövetni
- fature envy: ha más osztály methodjait/attr. használja mindig egy osztály
- data clumps: együtt járó adattagok, pl koordináták
- magic constants: egy szám, ami csak oda van téve, és nem tudjuk, mit jelent
- primitiv megszállotság: pl Stringet föl lehet használni mindenre - nem OOP
- switch ne legyen magasszintű logikában
- párhuzamos öröklési hierarchia: duplikáció (divergens ?)
- lazy class: már nincs is rá szükség, vagy nincs adattagokhoz művelet
- speculatív általánosítás: yagnit
- temporális attribútumok: null értéket kerülni
- hívási láncok: talán buildernél megengedett, de amúgy demeterszabály
- túl sok közvetítő: felesleges mediator pl, csak delegál egy osztály
- inappropriate intimacy: olyan mint irigység
- alternate classes with different inerfaces: pl ha 2 fejlesztő írta
- incomplete library class: hiányoznak megfelelő metódusai Date helyett Calendar
- Data class-ok nem kapnak felelősséget
- Refused Bequest: nem akarunk implementálni az ősosztály egy hasznos methodját
- comment: el kell gondolkozni, nem lehet-e anélkül érthetővé tenni

### Tesztelés
A legjobb ha van autómatikus JUnit tesztünk, és minden lépés után futtajuk

a teszt, mint kliens

##
### Composing Methods
#### extract method
- pl ha nincsenek egy absztrakciós szinten, itt sysout
- adunk neki nevet, akkor átgondoljuk, mi a felelőssége
- nehézsége függ attól, hogy használ-e, változtat-e helyi változón
#### Inline method
- az előző ellentéte
- figyelni kell, ha van örökléssel kapcsolatos probléma, akkor nem megoldható
#### Inline Temp
- mégse csináljunk egy temporális változót
- figyeljünk, hogy mindenhol helyettesítsük be
#### Replace Temp with Query
- hasonlo az előzőhöz, csak számítással helyettesítjük be (nem hívással)
- akkor is, ha számítást végez, nem akkora baj
- betesz változó helyére egy void method-ot, ami összeszorozza (számítást kisze)
#### Explaining Variable
- ez az ellentéte. hogy érthető legyen. érdemes final-re állítani őket
- jó nevet kell adni neki
#### Split Temporary Variable
- ha két felelőssége is van egy temp változónak
- régen még a memóriával kellett spórolni
#### Remove Assignments to Parameters
- inkább bevezetni egy új változót, és azon változtatni
- nem biztos, hogy mindenki tudja, hogy változik-e az eredeti..
#### Replace Method w/ Method Object
- kiszervezni sokváltozós methodot egy másik classba
- átadva önmagát? (this)
- nagy a kohézió, felelősslg ki van szervezve, teljesítmény nem para
#### Substitute Algorithm
- tenni egy lépést hátra és lecserélni egy egyszerűbbre


