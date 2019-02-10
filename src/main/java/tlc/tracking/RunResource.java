package tlc.tracking;

import com.google.cloud.datastore.*;
import com.google.cloud.datastore.StructuredQuery.*;
import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Get a token to test your application locally with datastore:
 * https://cloud.google.com/docs/authentication/production
 *
 *   1. Create a new service account with role owner https://console.cloud.google.com/apis/credentials/serviceaccountkey
 *   2. Download the key, we will refer to the path to the key as [PATH]
 *   3. Run: export GOOGLE_APPLICATION_CREDENTIALS="[PATH]"
 *   4. Run in the same terminal (not in your IDE): mvn appengine:run
 *
 *   Note: The id property is not the key, it represents the Run identifier. So, many entities can have the same id
 *   It just means that they are part of the same run.
 */


public class RunResource extends ServerResource {

    private Datastore datastore;
    private KeyFactory recordsKey;

    public RunResource() {
        datastore = DatastoreOptions.getDefaultInstance().getService();
        recordsKey = datastore.newKeyFactory().setKind("record");
    }

    /*
     * Enable you to convert a List object in an Array
     * It will help you pass lists to variadic functions
     *
     * In an alternative world where datastore.put() accepts Strings instead of Entities, we could write:
     * List<String> places = new ArrayList<>();
     * places.add("Buenos Aires");
     * places.add("Córdoba");
     * places.add("La Plata");
     * String[] placesArr = batch(String.class, places);
     * datastore.put(placesArr);
     *
     * You might need this function to do:
     *   1. batch operations with datastore.put()
     *   2. batch operations with datastore.delete()
     *   3. dynamically build CompositeFilter.and() (you must add some logic however,
     *      as "and" takes a fixed parameters before its vararg parameter - be clever :D)
     */
    private static <T> T[] batch(Class<T> c,  List<T> g) {
        @SuppressWarnings("unchecked")
        T[] res = (T[]) Array.newInstance(c, g.size());
        g.toArray(res);
        return res;
    }

    @Post("json")
    public void bulkAdd(RecordList toAdd) {
        /*
         * Doc that might help you:
         * https://cloud.google.com/datastore/docs/concepts/entities#creating_an_entity
         */
    	
    	List<Entity> recordsList = new ArrayList<Entity>();
    	for(Record r : toAdd) {
	        Key recKey = datastore.allocateId(recordsKey.newKey());
	        Entity record = Entity
	          .newBuilder(recKey)
	          .set("id", r.id)
	          .set("lat", r.lat)
	          .set("lon", r.lon)
	          .set("user", r.user)
	          .set("timestamp", r.timestamp).build();
	        recordsList.add(record);
    	}
    	Entity[] records = batch(Entity.class, recordsList);
        datastore.put(records);
    }

    @Get("json")
    public RecordList search() {
        /*
         * Doc that might help you:
         * https://cloud.google.com/datastore/docs/concepts/queries#composite_filters
         * https://cloud.google.com/datastore/docs/concepts/indexes#index_configuration
         * Check also src/main/webapp/WEB-INF/datastore-indexes.xml
         */

    	List<Filter> listFiler = new ArrayList<>();
        // Read and print URL parameters
        Form form = getRequest().getResourceRef().getQueryAsForm();
        for (Parameter parameter : form) {
            System.out.print("parameter " + parameter.getName());
            System.out.println("-> " + parameter.getValue());
            Filter propertyFilter = null;
            switch (parameter.getName()){
            	case "id":
            		propertyFilter =
                	    PropertyFilter.eq("id", Integer.parseInt(parameter.getValue()));
            		break;
            	case "loc":
            		String[] tab = parameter.getValue().split(",");
            		if(tab.length != 2) throw new IllegalArgumentException("Argument location non valide");
            		propertyFilter = 
            			PropertyFilter.eq("lat", Double.parseDouble(tab[0]));
            		listFiler.add(propertyFilter);
            		propertyFilter=
            			PropertyFilter.eq("lon", Double.parseDouble(tab[1]));
            		break;
            	case "user": 
            		propertyFilter=
            			PropertyFilter.eq("user", parameter.getValue());
            		break;
            	case "timestamp":
            		if(parameter.getValue().contains(",")) {
            			String[] t = parameter.getValue().split(",");
                		if (t.length != 2) throw new IllegalArgumentException("Argument timestamp non valide");
                		propertyFilter =
                			PropertyFilter.ge("timestamp", Long.parseLong(t[0]));
                		listFiler.add(propertyFilter);
                		propertyFilter =
                    			PropertyFilter.le("timestamp", Long.parseLong(t[1]));
            		}
            		else {
            			propertyFilter =
                    			PropertyFilter.eq("timestamp", Long.parseLong(parameter.getValue()));
            		}
            		
            		break;
            	default:
            		break;
            }
            listFiler.add(propertyFilter);
        }
        Query<Entity> query;
        QueryResults<Entity> results = null;
        if(!listFiler.isEmpty()) {
        	Filter firstFilter = listFiler.get(0);
        	listFiler.remove(0);
        	Filter [] arrayFilter = batch(Filter.class, listFiler);
        	query = Query.newEntityQueryBuilder()
            	    .setKind("record")
            	    .setFilter(CompositeFilter.and(firstFilter, arrayFilter)).build();
        	 results = datastore.run(query);
        }else {
        	query = Query.newEntityQueryBuilder()
            	    .setKind("record")
            	    .build();
        	 results = datastore.run(query);
        }
        List<Entity> listEntities = new ArrayList<>();
        results.forEachRemaining(entity -> listEntities.add(entity));
        
        RecordList res = new RecordList();
        for(Entity e : listEntities) {
        	Record r = new Record((int)e.getLong("id"),
        						 e.getDouble("lat"),
        						 e.getDouble("lon"),
        						 e.getString("user"),
        						e.getLong("timestamp"));
        	res.add(r);
        }
        
        return res;
    }

    @Delete("json")
    public void bulkDelete() {
        /*
         * Doc that might help you:
         * https://cloud.google.com/datastore/docs/concepts/entities#deleting_an_entity
         * You might to do one or more query before to get some keys...
         */
    	
    	String[] run_ids = getRequest().getAttributes().get("list").toString().split(",");
        
        for (String r : run_ids) {
            System.out.println("To delete :"+r);
            Filter propertyFilter=
            	    PropertyFilter.eq("id", Integer.parseInt(r));
            Query<Entity> query;
            query = Query.newEntityQueryBuilder()
            	    .setKind("record")
            	    .setFilter(propertyFilter).build();
            QueryResults<Entity> results = datastore.run(query);
            List<Entity> listEntities = new ArrayList<>();
            results.forEachRemaining(entity -> listEntities.add(entity));
            List<Key> listKeys = new ArrayList<>();
            for(Entity e : listEntities){
            	listKeys.add(e.getKey());
            }
            Key [] keys_tab = batch(Key.class, listKeys);
            //par defaut, on peut pas supprimer plus de 500 entites a la fois,
            //donc, on splitte en petits tableau de max 500 et on envoie des requetes delete de max 500
            if(keys_tab.length > 500) {
            	
				for (int i = 0; i < keys_tab.length - 500 + 1; i += 500) {
					Key [] keys_tab_splitted = Arrays.copyOfRange(keys_tab, i, i + 500);
					datastore.delete(keys_tab_splitted);
				}
            	if (keys_tab.length % 500 != 0) {
            		Key [] keys_tab_splitted = Arrays.copyOfRange(keys_tab, keys_tab.length - keys_tab.length % 500, keys_tab.length);
            		datastore.delete(keys_tab_splitted);
            	}
            }else {
            	datastore.delete(keys_tab);
            }
            
        }
        
        
    }
}





  