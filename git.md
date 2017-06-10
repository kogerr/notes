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

### Basic Commands
- `git init first-project` - létrejön könyvtár
- `git log` - kiírja commitokat
- `git log --oneline` - egyszerűbb
- `git log -3` - utolsó 3
- `git log --author name` / `git lof --committer name`
- `git help log` - kiadja az adott parancs dokumentációját
- `git show` - több mindenre jó. alapból megmutatja az utolsó commit diffjét
- `git diff` - jelenlegi diff local branchon

### gitignore
vim .gitignore
- bele nemkívánt file path-ja
- `git add .` -az összes fájlt hozzáadja
- `git rm` - töröl követett fájlt. kell commitolni is

### gitk
- `gitk &` - háttérben
- le lehet kérdezni, hogyan néz ki a repo

### gitconfig
3 szinten:
- system `/etc/gitconfig` - minden felhasználóra érvényes
- global `~/.gitconfig`
- local `.git/config` - projektre vonatkozó

- mindig alulról fölfele nézi, ha több van
- `git config --list` beállítot, beállítható dolgok (pl `mergetool=p4merge`)
- `git difftool`

### Object Database
- tárolja az összes tartalmat tömörítve
- SHA-1: 40 karakteres azonosító
- blob - fájlok
- tree: fájl struktúra (folderek)
- commitok
- ...

### Blob Tree
- fájlok és könyvtárak tömörítve:
  - fájl: méret és tartalom
  - tree: méret és tartalomjegyzék
- SH1 azonosító tartalomból generálódik: azonos fájloknál ugyanaz - egyszer lesz

### Commit:
- points to a tree
- szerző, committer, message és Parent commit

### Tag
- kiadja ügyfélnek az adott verziót, mindig arra a kommitra fog mutatni
- verzió tag, egy commitra hivatkozik +info: ki csinálta, comment...

### Data Model
- master branch is mutat mindig egy commitra
- commit-kor: csinál új commit-ot, master címkét átrakja rá (master file)
- remote: távoli repository, ez is mutat egy commitra
- HEAD: working areád hol van, ha csinálsz commitot, mihez fog fűződni (szülő)
  - master file-ra mutat, ami pedig commitra
- Objects are immutable
- References change

`git cat-file -p ******` - object database-ből SHA-1 eleje alapján
`git cat-file -t ******` - típusa

### Remotes
URLben:
- felhasználónév
- `@` után számítógép neve: `git.epam.com`
- `:` után projekt neve és `.git`

- `git remote add` - távoli repository fölvétele (clone= init + add)
- `git/refs/remotes` könyvtárba kerülnek a távoli branch-ek
- track-kelés: be lehet állítani egy távoli repot, amit követ és figyelmeztet
- rebase: átteszi legújabb commitra a cimkét

- új remote-nál:
  - `git init` név nélkül
  - commit
  - `git remote add...`
  - push

- `git remote -v` milyen címre mutatnak repók
- `git push -u origin master` -u: trackkelni fogja, távoli repo és branch
- fetch-kor átkerül a lokális origin/master címke
- `git rebase origin/master`
- `git pull --rebase` - egy lépésben ez

Pull request: leforkolt repository-ba pusholt commitra

### Git Branching
- elkülöníteni fejlesztéseket
- gitben csak egy 40 byte-os pointer, ami egy adott commitra mutat
- lokális művelet

- `git branch` létrehozni
- `git checkout` váltogatni
- `git merge` visszavezetni pl masterba

### Merge
- fast-forward: ha nem történt más commit, csak átrakja a master cimkét
- ha azóta volt változtatás, merge-öljük a kettőt össze (non-fast-forward)
- merge conflict feloldása:
  - 3-way merge: +base - az ahhoz képest történt változtatás kerül be
  - ha nem tudja, kérdez
