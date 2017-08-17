# Unit Testing and Mocking
- a tesztelés vizsgáló, feltáró folyamat a software minőségéről
- objektív (egyértelmű mérték) és független (egymástól)

validáció (mit), verifikáció (hogyan)
- működik
- megfelelő minőségű

- nem funkcionális: nem a futáshoz kapcsolódik, pl. hány soros methodok
- statikus: nem fut a kód (e.g. -"- )

### Functional and Non-Functional Testing
- Regresszió: Core Feature-öket tesztel, minden körülmények között
- Accaptence: kliens kattingat
- Alpha: kis csapat, jellemzően belső körből
- Beta: nagyobb merítés

Nem funkcionális
- stabilitás: mennyi ideig bírja újraindítás nélkül
- usability: elég intuitív-e
- Internationalization and localization: ugyanúgy megy-e máshol, más nyelven
- destruktív: kirúgni alkalmazás alól adatbázist, mit lát a user, újrafelállás

#### Unit Testing
legelső véfőbástya a bugokkal, hibákkal szemben. refactornál jó.
- egy method/function tesztelésére való method
- igaz/hamis
- amint kész vagy egy class-szal vagy methoddal, érdemes megírni hozzá a tesztet
- a termék része, együtt megy

#### Unit Testing Frameworks
- `@Test` annotáció jelöli JUnit tesztet
- `@Test(expected = RuntimeException.class)`
- `org.junit.Assert`: `Assert.assertTrue(condition);`
- `Assert.assertTrue("message", condition);` megjelenik reportban
- `Assert.assertTrue(expected, actual);` jobb oldalra kerül a method
- notequals, notNull, Same(referencia szerint ugyanaz), notSame, fail
- `Asser.fail();` bukjon el itt. pl ha nem dob exception-t

#### Unit Test Demo
- ugyanolyan nevű, de "Test"-re végződő osztályt létrehoz
- test method: "test" + method neve + mit kéne csinálnia
- létrehoz példányt (ő `underTest` néven...)
- teszt miatt kinyitni konstanst? eltörik a teszt, de érdemes inkább duplikálni
- a `@Before`-ral ellátott method minden egyes teszt előtt lefut. pl új példány
- egy teszt methodba csak egy methodot hívni

#### Professional Unit Testing and Mocking
- Given / when / then structure
- ne legyen fölösleges `@Before`
- ahány ciklikus.. van a methodban, annyi teszt ideális rá

#### Using Testing Frameworks
- lehet CI environmentben is futtatni
- lehet több `@Before` vagy `@After` is, de nem garantált a sorrend

#### Unit Testing Rules 2
- ne legyen benne ciklus/if/switch etc.

#### Problems with Dependencies
- integrációs teszt: több osztály kollaborációs munkája
- logger class-t felüldefiniálni, az összess methodot üresen
- `sum` class-t is felülírjuk. nem kell, h jót adjon vissza

## Mocking
- Stub: generált subclass tesztelésre
- Mock: stub, ami föl is jegyzi rec/replay listába, `verify`-olni lehet
- mockista: verify-ol is, nem csak értéket nézi: biztosabb, de lassabb
- void-ot sokszor csak mock-kal lehet
- `.times(int)` `.atLeastOnce()` `.anyTimes()` `andReturn()` `andStubReturn()`
`andThrow()`
- ha nincsen verify: észreveszi, ha több hívás van replay-ben, mint rec-ben,
vagy nemvárt hívás. de azt nem, ha nem elegendő hívás.
- argument matcher:
  - `EasyMock.anyObject(String.class).andReturn(TEST_TIMESTAMP)` - mindig
ugyanazzal tér vissza
  - `EasyMock.matches("[a-z]+")`
  - `EasyMock.or(EasyMock.matches("[a-z]+"), ..., ...)`
  - `EasyMock.lt()` - less than
  - `EasyMock.not()`
- `EasyMock.checkOrder(service, true)` - ezután számít a (rec) sorrendiség
- `EasyMock.createControl()` - mock tároló zsák
  - `control.createMock(...)` - így hozunk létre mock-okat
  - `control.checkOrder(true)` - vizsgálja a sorrendiséget mockkok között
  - `control.replay()` - az összeset egyszerre tudjuk kapcsolni
- EasyMock-hoz: asm, cglib. objenesis, easymock jar-ok kellenek
- SUT: System Under Test
- behaviour test-ben stub nem tud segíteni, mert nem vizsgálja az interakciókat
- mock megjegyzi, mi történt bele, be lehet injektálni a rendszerbe
- `createNiceMock` - mintha stub lenne
- `createStrictMock` - mindig be van kapcsolva sorrendiség
- ha egy argumentumot argument matcher-ként adunk meg, akkor mindet úgy kell
  - ilyenkor `EasyMock.eq(...)`
- dinamikus a mock: runtime jön létre, nincs a file rendszerben manifesztációja

- null-ra is érdemes tesztelni
- abstract class: amiben van logika, tesztelni kell (kivéve accessorok)
  - testben implementálni
  - statikus validate method-ot úgy teszteli, mintha ide tartozna (null-ra)
  - ezért nem kéne komplex statikus method-okat írni (csak apró utility-ket)
- final dolgokat nem lehet mockkolni
- SL4J teszt moduljával is lehet loggolást ellenőrizni
- vagy akár LoggerFactory-ban singleton mock loggert tesztből beállítani

- GUI, filesystem, stb esetén tegyünk elé interface-t, hogy tesztelhető legyen

#### Mockito
- egyetlen jar
- deklarálás elé `@Mock` annotáció, `MockitoAnnotations.initMocks(this)`
- `BDDMockito.verify(service).release();`
- `BDDMockito.given(service.getName()).willReturn(TEST_SERVICE_NAME);`
- kiindulásnál mindig nice mock, csak aztán verify-oljuk, h történt-e hívás
- given/willReturn-nel egymás után több ha-akkor beállítható. kiszervezhető
- `anyObject(String.class)`-ra van `.anyString()` is
- itt is van `.matches("regex")`
- `.willThrow()` exception-ökre
- `InOrder` class a sorrendiségre:
  - `inorder.verify(service).connect()` - sorban ellenőrizni rajta a hívásokat
- `Capture<StringBuilder> content = new Capture<StringBuilder>();`
  - `EasyMock.capture(content)`
  - `content.getValue().toString()`
- ue. Mockitoban:
  - `ArgumentCaptor<StringBuilder>` `ArgumentCaptor.forClass(...class)`

#### TestNG
- argumentumon keresztül értékpárok sorozatát adni tesztnek:
  - `@DataProvider(name = "ultimateInputs")`
  - `@Test(dataProvider = "ultimateInputs")`
- `@BeforeMethod`, `@BeforeClass`, `org.testng.Assert`