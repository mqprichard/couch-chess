CouchDB version of REST chess server demo

The Ektorp framework requires the following View document, which is not auto-generated in this version
Add using Futon or curl as described here: http://guide.couchdb.org/draft/design.html
{
   "_id": "_design/Move",
   "views": {
       "by_game": {
           "map": "function(doc) { if (doc.game) emit(doc.game, doc); }"
       }
   }
}