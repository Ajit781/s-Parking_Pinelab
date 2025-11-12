package utilities.async_tasks;

/**
 * Created by vyomaPC1 on 2/13/2018.
 */

public class RouteLocationsTask /*extends AsyncTask<Void, Void, ArrayList<RouteListBean>>*/ {

    /*private RouteListAdapter adapter;
    private ArrayList<RouteListBean> itemsList;
    private ArrayList<String> routeFiles;
    private ListView listview;
    private Context context;

    public RouteLocationsTask(ListView listview, RouteListAdapter adapter, ArrayList<String> routeFiles, Context context)
    {
        this.adapter = adapter;
        itemsList = new ArrayList<RouteListBean>();
        routeFiles.add("0");
        this.routeFiles = routeFiles;
        this.context = context;
        this.listview = listview;
    }

    @Override
    protected ArrayList<RouteListBean> doInBackground(Void... params) {
        for (int i = 0; i < routeFiles.size(); i++) {

            File loadFile = new File(Environment.getExternalStorageDirectory()+"/LocationTracker/Routes/"+routeFiles.get(i));
            try {
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new FileReader(loadFile));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                //while ((line = br.readLine()) != null) {
                String firstEntry = br.readLine();
                Barcode.GeoPoint startPt = getGeoPointFromString(firstEntry);

                String lastEntry = tail(loadFile);
                Barcode.GeoPoint endPt = getGeoPointFromString(lastEntry);

                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> addresses;

                addresses = geocoder.getFromLocation(startPt.lat, startPt.lng, 1);
                String startPtStr = addresses.get(0).getLocality();

                addresses = geocoder.getFromLocation(endPt.lat, endPt.lng, 1);
                String endPtStr = addresses.get(0).getAddressLine(0);

                itemsList.add(new RouteListBean(String.valueOf(startPt.lat), String.valueOf(startPt.lng),startPtStr,1 ));
            }
            catch(FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        return itemsList;
    }

    public void onPostExecute(ArrayList<RouteListBean> itemsList)
    {
        super.onPostExecute(itemsList);

        adapter.setItemList(itemsList);
        adapter.notifyDataSetChanged();
        listview.setAdapter(adapter);

    }

    public Barcode.GeoPoint getGeoPointFromString(String coordinates)
    {
        String loadedLatStr = coordinates.substring(coordinates.indexOf("(") + 1, coordinates.indexOf(","));
        String loadedLongStr = coordinates.substring(coordinates.indexOf(",") + 1, coordinates.lastIndexOf(","));

        double loadedLat = Double.parseDouble(loadedLatStr);
        double loadedLong = Double.parseDouble(loadedLongStr);

        return new Barcode.GeoPoint(1,loadedLat, loadedLong);
    }

    public String tail( File file ) {
        RandomAccessFile fileHandler = null;
        try {
            fileHandler = new RandomAccessFile( file, "r" );
            long fileLength = fileHandler.length() - 1;
            StringBuilder sb = new StringBuilder();

            for(long filePointer = fileLength; filePointer != -1; filePointer--){
                fileHandler.seek( filePointer );
                int readByte = fileHandler.readByte();

                if( readByte == 0xA ) {
                    if( filePointer == fileLength ) {
                        continue;
                    }
                    break;

                } else if( readByte == 0xD ) {
                    if( filePointer == fileLength - 1 ) {
                        continue;
                    }
                    break;
                }

                sb.append( ( char ) readByte );
            }

            return sb.reverse().toString();
        } catch( java.io.FileNotFoundException e ) {
            e.printStackTrace();
            return null;
        } catch( java.io.IOException e ) {
            e.printStackTrace();
            return null;
        } finally {
            if (fileHandler != null )
                try {
                    fileHandler.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }*/
}