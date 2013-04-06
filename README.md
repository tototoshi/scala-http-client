```scala
scala> import com.github.tototoshi.http._
import com.github.tototoshi.http._

scala> val c = new Client
c: com.github.tototoshi.http.Client = com.github.tototoshi.http.Client@2c3a9ea0

scala> c.
     |   get("http://search.twitter.com/search.json").
     |   params("q" -> "scala").
     |   execute.
     |   asJson
res0: org.json4s.JValue =
JObject(List((completed_in,JDouble(0.037)), (max_id,JInt(320528570885754880)), (max_id_str,JString(320528570885754880)), (next_page,JString(?page=2&max_id=320528570885754880&q=scala)), (page,JInt(1)), (query,JString(scala)), (refresh_url,JString(?since_id=320528570885754880&q=scala)), (results,JArray(List(JObject(List((created_at,JString(Sat, 06 Apr 2013 13:29:01 +0000)), (from_user,JString(M14367)), (from_user_id,JInt(404327254)), (from_user_id_str,JString(404327254)), (from_user_name,JString(Mark Jordison)), (geo,JNull), (id,JInt(320528570885754880)), (id_str,JString(320528570885754880)), (iso_language_code,JString(en)), (metadata,JObject(List((result_type,JString(recent))))), (profile_image_url,JString(http://a0.twimg.com/profile_images/3476765632/a2286950...
```
