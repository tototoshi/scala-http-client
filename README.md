```scala
scala> import com.github.tototoshi.http.Client
import com.github.tototoshi.http.Client

scala> val c = new Client
c: com.github.tototoshi.http.Client = com.github.tototoshi.http.Client@2c3a9ea0

scala> c.GET("http://www.google.com/search", Map("q" -> "homuhomu")).asString
res0: String = <!doctype html><head><title>homuhomu - Google Search</title><script>w.....

scala> c.GET("http://www.google.com").statusCode
res1: Int = 200

```
