### `.paralellStream()`
- árnyoldal: 7 prgramozó...
- akkor éri meg, ha kitermeli a párhozamítás költségét
- csak nagyon nagy mennyiségű adatnál működik
- háttérben a `splterator`-t használja

### `LongAdder()`
- gyorsabb, mint `AtomicLong`, mert a háttérben több számlálót tart karban
- megpróbál annyi számlálót létrehozni, amennyi szál van
- amikor az eredményt lekérjük, összegzi
- mindegyik szál egymástól függetlenül adogatja a saját változóját

`java.util.concurrent` csomag mind a többszálúságra próbál megoldást adni

### Method Paraméterek neve
- elveszik, nem látszik, ha megnézzük `.getName()`-mel
- régen debug információval látták el, hogy meglegyen a neve
- fordítási paramétert kell megadni: `javac -parameters`
- eclipse: Preferences > Java > Compiler > Store... method parameters

### Annotációk
- `public @interface valami` így lehet annotációt létrehozni
- előtte `@Target(ElementType.TYPE)` hogy mire lehessen rátenni
- nem volt: többet is rá lehet tenni: `Repeatable(value = valami.class)`

### JS futtató API: Nashorn
```java
ScriptEngine engine = ScriptEngineManager().getengineByName("nashorn");
engine.eval("print('Hello World!');");

engine.eval(new InputStreamReader(Nashorn.class.getClassLoader()
.getResourceAsStream("filter.js")));
```
- a Rhino-hoz képest: gyorsabb, nem elavult
- lehet `.js` fájlokat is behúzni
- `javax.script.ScriptEngineManager`, stb...