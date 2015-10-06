<?xml version="1.0"?>
<rss version="2.0">
  <channel>
    <title>Catalog</title>
    <link>http://${host}:${port}/</link>
    <description>New products</description>
    <#list products as product>
        <item>
           <title>${product.name}</title>
           <link>http://${host}:${port}/${product.uuid}</link>
           <uuid>${product.uuid}</uuid>
           <name>${product.name}</name>
           <supplier>${product.supplier}</supplier>
           <price>${product.price}</price>
           <dateAdded>${product.dateAdded?date}</dateAdded>
        </item>
    </#list>
  </channel>
</rss>