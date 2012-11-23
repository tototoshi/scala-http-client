```scala
scala> import com.github.tototoshi.http._
import com.github.tototoshi.http._

scala> val c = new Client
c: com.github.tototoshi.http.Client = com.github.tototoshi.http.Client@2c3a9ea0

scala> c.post("http://www.google.com/search").param("q", "homuhomu").execute.asString
res0: String = <!doctype html><head><title>homuhomu - Google Search</title><script>w.....

scala> c.post("http://www.google.com").execute.statusCode
res1: Int = 200

```
