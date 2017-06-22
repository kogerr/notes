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

### Moving Features Between Objects
#### Move Method
- a vele együttjáró adatokat azt nem akarjuk-e a methoddal együtt mozgatni
- de általában másik osztályhoz tartozó adatokon dolgozik
- meghagyjuk az eredetit? ha igen, delegálás
- figyelni kell, nincs-e öröklés, nincs-e felüldefiniálva
#### Move Field
- figyelni, pl ha `protected` a láthatósága, használja-e a leszármazott
- encapsulate field: getterrel és setterrel érjük el
#### Extract Class
- ha nagyon sok attribútuma van, vannak, amik szorosan összekapcsolódnak
- pl person és telefonszám
- ha változik az osztály felelőssége, figyeljünk, hogy átnevezzük
- szükséges lehet, hogy összekössük a két osztályt egy attribútummal
#### Inline Class
- Egy lusta osztályt be lehet helyettesíteni
#### Hide Delegate
- hogy a Demeter szabályt be tudjuk tartani, dependenciát eltüntessük
#### Remove Middle Man
- az előző fordítva
#### Introduce Foreign Method
- kiszervezve egy utility class-ba, w/ instance of the server class as argument
- cél elkerülni az ismétlődést
#### Intorduce Local Extension
- örökölve megoldani
- de akkor másik osztályt kell példányosítani és lehet, hogy változik az ős

### Organizing Data
#### Self Encapsulate Field
- Getterrel-setterrel elérni az attribútumokat: csk úgy jó, ha nincs mindenhez
#### Replace Data Value with Object
- primitive obsession: van, hogy csak valami primitívet használnak....
- helyetadni a műveleteknek
#### Change Value to Reference
- megengedjük, hogy ne legyenek immutable-ök
#### Change Value to Reference
- ha rájövünk ha csak valami értéket tárol
#### Replace Array with Object
- tömbben különböző értékek.. hozzunk létre rá egy osztályt (koordináták pl)
#### Replace Magic Number with Symbolic Constant
- elnevezni a konstanst a jelentése után
#### Encapsulate Field
- külső szemlélő számára elrejtjük a publikus attribútumokat
#### Encapsulate Collection
- ha csak olvasható nézetet akarunk egy kollekcióra, nem adhatunk rá gettert
- kell rá írni add/remove methodokat
#### Replace Type Code with Type / Subclass
- típuskódokat kerülni kell, hogyha módosíthatják a működést
- enumba vagy származott osztályokba kiszervezni
#### Replace Type Code with Strategy
- hogyha dinamikusan változhat
#### Replace Subclass with Fields
- van, hogy a subclassok túl nagy bonyodalmat keltenek, nincs rájuk szükség
- egyszerűbb, hogyha ősosztályban eltároljuk valahogy