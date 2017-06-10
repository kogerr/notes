# Git

### Source Control
#### központi Version Control System Server:
- megvan minden egy központi helyen, tudunk együtt dolgozni, backupot csinálni
- ilyen: SVM, CVS, (IBM) ClearCase
- ütközés problémája fölvetődik, erre ad megoldást minden VCS valahogy
- mindenki 1 konkrét verziót kiszed

#### Distributed Version Control System
- Git ilyen.
- minden gépen: teljes verzió adatbázis
- saját gépemen választhatok abból, amelyiken dolgozni szeretnék
- két gép között is lehetne kapcsolat szerver nélkül
- mivel csak időnként kommunikálunk a szerverrel, hatékonyabb a hálózati kommunikáció, sok minden lokális

### GIT
Short Story:
- a Linuxhoz találták ki
- először BitKeeper

What is git?
- distributed revision control and source code management system
- nem SVN
- mindig készül egy új snapshot a fáról

### Essentials
- Non-linear developement
  - egyszerre dolgozhatunk több különböző témán, feature-ön (branch-ek)
- Distributed developement: nem zavarjuk egymást
- Efficiency: sokkal kevesebb helyet foglal, mint az SVN - tömörít
  - hatékony hálózati műveletek (kevés, és tömörítve)

### Prerquisites
https://www.perforce.com/downloads/visual-merge-tool

### Basics
snapshots:
- kiindul az első verzióból, eltárólja az új verziókat a módosított fájlokból

### Versioning
- working directory-ban az van, amivel épp aktuálisan dolgozunk (checkout)
- Staging area/index: mi fog belekerülnia  következő kommitba
- Repository: egész verziózott adatbázis metadatával: .git directory

### Lifecycle
1. untracked - add ->
2. unmodified - edit ->
3. modified - stage ->
4. staged - commit -> unmodified




