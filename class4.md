# class 4
szonkronnál blokkolva vagyok, amíg le nem fut
lazán akarjuk csatolni a rendszereket, üzenetsor, queue szeparált
absztrakció az adatbázis: a való világ leképezése, egy osztály is
azonos metódus máshogy viselkedhet: polimorfizmus, mindegy melyik implementáció
interface 

_Composition:: kerék önmagában nem értelmes, végtagok magukban nem létezhetnek, magában nem jöhet létre
ha szülő meghal, gyerek is meghal, pl ház szobái: ház szobái nem léteznek ház nélkül

_Aggeregation_: magábafoglalás, pl emloyee címe, nem ő hozza létre, de hozzá tartozik
saját lifecycle megvan minden objectnek
gyerek object nem tartozhat másik objecthez

_Association_: parkolóhely, autó és alkalmazott
teacher student-et összerendeljük

_Dependency_: amikor valaki használ valamire, pl kiírni a konzolra, ahhoz kell egy osztály, ami kiír a konzolra, függeni fogok a `System.out`tól, használom az osztályt valamire, nincsen köztük semmi reláció

mindig a compositiont fogjuk favorizálni a kiterjesztéssel szemben, többszörös öröklődés helyett is lehet

single responsibility: különben nehezen lesz frissíthető és tesztelhető. ha jó, jól áttekinthető, letisztult

open-close: négyszög és kör
dependency: pl megrendelő és record, alacsonyabb osztályok beteszik az adatbázisba stb... engem nem érdekel, hogyan tárolja, nem akarok attól függeni, adjon egy interface-t, lehet, hogy időnként váltogatni akarom

# class 5
tell dont ask: toString
value object: konstans értéket szimbólizál, ne változtássa meg senki. mintha primitív értékek lennének
invariánsok:: feltételek, amiknek fönn kell állnia. pl adott dependencia álljon rendelkezésre
_példa_

reuse-release: csak újrafelhasználható osztályok legyenek ú~ csomagban
aciklikus...: ha körbehivatkoznak, lehetnének egyben is inkább

SportBetting:
hiába változik a szorzó, nekem az marad, amikor fogadtam
lehet játékosra meg tenisz meecsre is fogadni
wager maga a fogadás, amit letesz a játékos
az outcomeOddra fogad
csinálni kell egy domain modellt
betöltve tesztadatot a memóriába (meccsek)
felhasználó beírja, mikre fogad, mennyivel
Resultból kigenerálni saját Outcome-jaimat
felhasználóadminisztráció: csak `Player` kell






