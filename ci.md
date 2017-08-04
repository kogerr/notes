# Continuous Integration
adminisztrációs feladatokat levenni a fejlesztők válláról érték érdekében
## Git
- történetiségkezelés
- konkurens modifikáció

GitHub flow:
- master, release és feature branchek
- mastert bármikor lehet release-elni
- minél rövidebb és kevesebb feature branch
- minor fixes on master

## Maven
project management tool with project object model
- dependency management
- project lifecycle
- cross-cutting logikákat kiszervezi egy közös helyre
  - pl. logolás
  - tesztelés, fordítás is a projectet behálózó konfigurációt eredményeznek
- egy projecthez egy p.o.m. tartozik, ebben van minden
- egy tool ami elfogadható default működést tud kínálni
- Common Interface: egységes belépési pont, amin keresztül egy sor műveletet el tudunk végezni
- Maven Plugins: disztribúcióban nincs szinte semmi

Conceptual Model of a "Project"
- dedikált `pom.xml`
- leíró nyelvezetében csak azt kell konfigurálni, amiben eltérünk a defaulttól
- dependency management: sok erőforrást emészt föl
  - másik csapat upgrade-el pl
- Remote repositories
- Universal reuse of rebuild logic: konfiguráció öröklés, egységesítés
- Tool Portability / Integration
- Easy Searching and Filtering

Declarative vs. Imperative
- azt adjuk meg, mit akarunk elérni
- pom.xml azt mondja meg, mi történjen meg a lifeCycle-ökben
- deklaratívan konfiguráljuk

### Default Lifecycle
kulcs-lépések
- validate: formailag helyes-e
- compile
- test
- package: rész eredményeket összeccsomagoljuk egészbe
- integration test
- verify
- install
- deploy (repository-ba)

### Installing Maven
- `mvn archetype:generate`
- `DgroupId` mi legyen az alap identifiere
- `DarchetypeArtifacetId` a template ami alapján project struktúrát létrehoz

`pom.xml`:
- `groupId`,`<artifactId>`,`<version>` azonosítják a projectet
- packaging: jar/pom/zip/ear
- `<name>` szimbólikus név, így fog rá hivatkozni
- `<dependency>` - scope-ja `test` - tesztelés időben lesz ott

### Using the Maven Help Plugin
- Available help goals:
`mvn help:describe -Dplugin=help`
- Effective POM: kiírja default-okkal együtt
`mvn help:effective-pom`
- Compiler plugin goals:
`mvn help:describe -Dplugin=compiler`
- Compiler plugin compile goal parameters:
`mvn help:describe -Dplugin=compiler -Dmojo=compile -Ddetail`

### Compile Project
target folder: a maven workspace-e, mindig ez alá dolgozik
- tetszőlegesen törölhető, újra létrejön
- nincsen verziókezelés alatt: `.gitignore`
- `mvn clean package` - létrehozza a jar filet - concatenált: először töröl
- ha egy fázist futtatunk, az összes korábbi fázis lefut
- csak a gólokat: `mvn clean:clean compiler:compile compiler:testCompile
surefire:test jar:jar`

#### The jar plugin
- in package lifecycle
- uses *Maven Archiver*
- `<mainClass>` belépési pont

#### Repositories
- Local: `c:\users\${myuser}\.m2` - használt pluginok, saját fordított termékek
- Remote (Internal for organization) - nem publikus termékek vállalati repoból
- Remote (Public) `http://search.maven.org/`-ről tölt minden artifact-ot

- hivatkozhatunk dependency-re és hogy milyen verziót szedjen le
- a vállalatit konfigurálni kell
#### Download dependencies & plugins
- sorrendben keres: local repo, remote repo, central repo

#### Install Project to Local Repository
`install`
- jar és .pom stb ott van a local repository-ban

#### Repository Deployment Automation
- pom-ba a repo
- `~/.m2/settings.xml` - váz
- `$M2_HOME/conf/settings.xml` - minden projectre érvényes alap konfigurációk

#### Versioning
főleg ha olyan jarokat építünk, amiket mások is haználnak
- fontosak verziózási stratégiák
- major.minor incremental : 1.00
- incremental: transzparensen u.úgy működik kifele. pl:refaktorálás, tisztázás,
performance, tesztek...
- minor: visszafele kompatibilis. új feature-ök
- major: mérföldkő, teljesen más API
- literal SNAPSHOT: még fejlesztés alatt áll. ezen a néven születhet más is
- léptetni a pom.xml `<version>` alatt lehet
- versions-plugin segít léptetni
  - explicit le kell tölteni (tuti?)

#### Profiles
profilokba lehet szervezni konfigurációkat a pom.xml-en belül
- bizonyos körülmények mellett be/ki van kapcsolva egy profil
- CLI-ből `-P`
- környezeti változó hatására aktiválódó is lehet

#### Dependency Resolution
gyárilag hozza a dependency kezelés képességét
- project-ek egyedi azonosítóján keresztül tudunk rájuk utalni mint dependency
- provided: kell a fordításhoz, de nem kell az előálló állományba,pl Servlet API
- Runtime: :point_up: fordítva. fordításkor nem kell, de futás időben. pl dll-ek
- dependencia mátrix: tranzitív dependenciák kombinációjára - legrövidebb út pl
- dependency-plugin: `dependency:tree` fa nézetben a dependency-k, mit használ

### Multi-Module Project Example
#### Multi-Module Project structure
- legfelül pom.xml
- training API modul
- webes modul az implementációkat aggregálja és XMLes leíró fájlokat mögé tegye
- integration teszt modul: lehet ellenőrizni, h tényleg megjelennek-e a nevek
- konkrét implementációkat tároló modulok középen (participant api?)

#### Multi-Module Projects
- van egy root folder, amiben van pom.xml, azon belül további projektek
- alfoldereknek is vannak pom.xml-ek és maven project-ek
- pl ebből egy darab EAR fájl lesz - package fázisban összeszedi jar-okat
- két szintje van: inheritance vs aggregation
- aggregation:
  - projektben tudunk hivatkozni modulokra
  - modulok helyet fognak foglalni az aggregátoromban
  - bármit csinálok, kiértékelődik ezekre a modulokra is
  - `clean install` ezután alprojekteket is installálja
  - egy belépési pontból tudunk vezérelni nagyobb rendszereket
- zöld nyilak: modularitás, piros: dependency
- war? file épült a példában

#### Configuration Inheritance
- modul meghivatkozza parent-et - megörökli konfigurációt
- `<properties>` `<maven.compiler.version>1.8` javac ezt kapja
- `<plugin>` résznél a compiler plugin
- `<parent>` tagben lévő konfigurációt beállítja
- aggregáció (modularizálás): több modult együtt futtatni. lefut almodulokra is
  - ez még nem jelent automatikusan konf. öröklést, az az inheritance
- nem kell feltétlenül az almodul parentje legyen a fő
- `mvn jetty:run`-nal futtatja a jetty szervert
- `<build>`-ben fail-safe plugin
  - be van állítva: megnézi properties-ben a `<skip.integration.tests>` értéket
- `mvn clean verify -P itests` beállítja itests profilt és teszteli böngészőben
- `settings.xml`-nek is van hierarchiája - workspace-ben is lehetne

## Nexus
repository manager: bináris állományok menedzselését segíti
- érdemes mindenhol, ahol van Maven
- Artifactory is repository manager
- tudja csökkenteni a külvilággal hálózati forgalmat: vállalati cahce-ből
- saját kézben van az uptime, nem függ külső repository-tól
- GAV search: Group Artifact Version
- classname search is van
- lehet törölni, vagy kézzel letölteni jar-t
- LDAP szerverektől tölti le a jogosultságokat

- hosted: fizikailag a fájlrendszerben tárolja a bináris állományokat
- proxy: átjáró másik távoli repo és a vállalati között, beállított sync.
  - megnézi proxy, hogy megvan-e a cache-ében, és ha nincs, elmegy
- virtual: repositry adapter

Repository Group
- adott sorrendben teszi meg artifact resolution
- egy repository-ként látjuk
- ha kérünk valamit sorrendben keresi

Nexus hosted repositories
- repository policy: snapshot - ide snapshot-ok kerülnek

## CI Server - Jenkins
CI támogató szerveroldali alkalmazások
- process-ek automatizálása, pipeline, történetiség, log...
- hálószerűen beszövi CI keretrendszerünket
- egységes interface
- pl Atlassian Bamboo..

Jenkins:
- ingyenes
- sok ingyen plugin - testreszabható
- publikus API: meg is lehet írni
- java nyelven íródott: a Sun fejlesztette Hudson néven
- project-nek hívja a process-eket
- build executor service: milyen process-ek futnak
- agenteket állít sorba, azokon keresztül futtat folyamatokat
- masterhez nincs agent, 2 folyamatot tud párhuzamosan futtatni
- green balls plugin
- tart egy local repository-t a jenkins szerveren, aztán deploy-ol Nexusba
- van plugin Mavenhez, Graddle-höz, de futtathatunk shell scripteket is
- ssh kulccsal kapcsolódik git-re
- meg van adva, milyen branch-re fussion
- Build Triggers: mely mechanizmus hatására induljon el: összekötni process-eket
  - lehet időközönként, vagy git hook-ra

új project:
- Jenkins -> New item -> Copy existing
- nem enged perjelet
- lehet több branchre egy job-ot fölvenni is akár

## Deployment
target server-re eljuttajuk bináris állományt, elindul
- Jenkins Nexusból lehúzza az állományt és fölteszi távoli szerverre (pl Tomcat)

## Release
- konzisztenciát, történetiséget megtöri, ha SNAPSHOT függősége van release-nek
- attól függetlenül, hogy beleépítjük
- belső SNAPSHOT függőségeket fölhúzzuk release verzióra
- `release` két fő goalja: `prepare` `perform`
  - preparation: felhúzni verzióra, git tag, és aztán következő SNAPSHOT-ra
  - perform: git tag-et check-olja ki és clean deploy
- `-DdryRun`: tesztelés, nem megy ki saját gépünkről
- master branch-ről release-elünk
- Jenkins release job
  - kicsekkolja a git tag-es kódot
  - manuális, nem triggerre
  - `release:prepare release:perform --batch-mode` == nem interaktív
  - build is parameterized: pl release ranch name