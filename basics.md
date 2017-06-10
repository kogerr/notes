# Compilation

javadoc valami.java -d konyvtar
sysout - standard output
kilépési kód - le lehet kérdezni
batch: -1 esetén SMS a rendszergazdának pl
meg lehet változtatni output streamet
platform független bytecode: .class

### javac:
javac [options] [sourcefiles] [@argfiles]
- d		path
- classpath	define classpath - hozzá lehet adni classokat,
amik kellenek a classhoz, amit fordít
- version	compiler version-t kiírja
- OS wildcards: pl: java *.java
@argfiles	used for large number of arguments

### Jar:
ZIP with meta-inf/manifest.mf
futtatható jar file-ok: meg kell adni belépési pontot (a manifestben)
jar cf myJar.jar *.class	- becsomagolja
jar uf myJar.jar 		- update
jar xf myJar.jar		- kicsomagolni
jar tf myJar.jar		- test
-v 	verbose
-f	IO file
-m	manifest file
-C dir	target directory
-e	executable
jar cfe ivaders.jar Game *.class *.gif
Game a belépési pont

### Execution:
java [options] myClass [arguments]
java [options] -jar myJar.jar [arguments]
-classpath / -cp	override CLASSPATH environment variable
-Dkey=value		define or override system variable (globális változó)
(System.getProperty("key1")) 	[Dkey1=.....]
-version
-Xms128m		initial memory allocation pool size
-Xmx1024m		maximum memory allocation pool size - leáll

### etc
jconsole
jvisualvm
javap valami.class
jdb	java debugger

### Packages
hierarchikus szerkezet - ezek majd alkönyvtárak lesznek
packageben ponttal elválasztva a szintek
a gyökér a default package
java és javam nem lehet
lehet importálni az osztályokat, és akkor nem kell kiírni
explicit kell importálni pontosan
*static import* : az osztály egy method-ját meg lehet hívni mintha
a saját osztályomban lenne
package com.epam.training.HelloWorld;
javac com/epam/training/HelloWorld.java
java com.epam.training.HelloWorld
Recoursive compilation: @source.txt

### Statements
Trenery operator ( ) ? .. : ...
Reserved words...

### String
null does not equal .isEmpty
a == b 		referenciaérték megegyezik
a.equals(b)
CharSequences: StringBuilder, StringBuffer,.... 
mutable
StringBuffer szálbiztos (thread safe)
StringBuilder gyorsabb

### Primitives
have Wrapper class
default value (0, false)
cannot be null
from String: .parseInt
Integer.valueOf() vagy autobox ( = ) vagy new Integer()
az első két esetben a cache-selt objektumot hozza létre, hogyha 127 alatt

### Class
ClassName.this.variable - utalni az inner classból outer értékeire
egy .java file-ban egy public class lehet

### Constructor
default constructor - csak ha nem definialunk neki
super(...)	call super class' constructor
this(0,0)	hivatkozni másik constructorra
System.gc(),	magától is
finalize()	akkor fut le, amikor az object megszűnik

### Object
ha equals akkor a hashCode-juk is megegyezik
de ha nem equals, attól még lehet ugyanaz a hashCode()
argumentben int de Integerrel hívjuk meg, akkor autounbox

### Inheritance
no multiple inherintance
amstract class: nem lehet példányosítani
abstract method: nincs implementálva
access modifiers allow for more but not less access (in subclass)
Interface: pre-Java8: pure abstract classes

### Annotations
use cases: 
- for the compiler
- compile-time / deployment time
- runtime
@Deprecated:	csak kompatibilitási okokból, de nem ajánlott.
Illik ajánlani helyette mást
@SuppressWarning
you can make your own:
@interface
csak konstanst lehet hívni
generált javadocban látszik

### Exceptions
catch block:	LOGGER
multi-catch
trx-with-resources	meghívja a .close()-t, Closable interface

### Stream
byteStram: -1 -et ad vissza a file végén
Character Stream
StringTokenizer / StramTokenizer
inputStream = new BufferedReader(new FileReader("xanadu.txt"));

### Generics
only at compile time
cannot be primitive
autoboxing, unboxing
null - cannot unbox

### Collections
<T> - Map-nél key and value too
Comparator interface
könnyen lehet konvertálni egyikből a másikba
toArray
comparable interface for own types if sorting
comparator for sorting by sg



