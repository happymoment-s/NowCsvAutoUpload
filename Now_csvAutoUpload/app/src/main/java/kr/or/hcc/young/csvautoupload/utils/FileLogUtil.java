package kr.or.hcc.young.csvautoupload.utils;

public class FileLogUtil {
    /*public final static String SLBS_FOLDER_NAME = "slbs";
    // [logcat_날짜_앱패키지명_모델명_os버전]
    public static String LOG_CAT_FILE_NAME = "logcat" + "_" + new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis());
    public final static String BEACON_LOG_FILE_NAME = "beaconLog";
    public final static String BEACON_SIMULATION_FILE_NAME = "beaconSimulationLog.csv";
    public final static String DUMMY_SIMULATION_FILE_NAME = "dummySimulation.csv";

    public static List<BeaconSimulationData> readBeaconSimulationLog() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        List<BeaconSimulationData> beaconSimulationDataList = new ArrayList<>();

        try {
            String path = Environment.getExternalStorageDirectory() + "/" + SLBS_FOLDER_NAME +  "/" + BEACON_SIMULATION_FILE_NAME;
            CSVReader csvReader = new CSVReader(new FileReader(path));
            String[] nextLine;
            int time = 0, BSSID = -1, UUID = 1, Major = 2, Minor = 3, RSSI = 4; //index...
            while ((nextLine = csvReader.readNext()) != null) {
                if (nextLine.length < 5) continue;
                if (nextLine[0].contains("time")) { //index 설정
                    time = 0;
                    for (int i = 0; i < nextLine.length; ++i) {
                        if (nextLine[i].contains("BSSID")) BSSID = i;
                        else if (nextLine[i].contains("UUID")) UUID = i;
                        else if (nextLine[i].contains("Major")) Major = i;
                        else if (nextLine[i].contains("Minor")) Minor = i;
                        else if (nextLine[i].contains("RSSI")) RSSI = i;
                    }
                    if (time < 0 || UUID < 0 || Major < 0 || Minor < 0 || RSSI < 0) break;
                    continue;
                }

                Date date = sdf.parse(nextLine[time]);
                long timeStamp = date.getTime();
                String uuid = nextLine[UUID].toUpperCase();
                int major = Integer.parseInt(nextLine[Major]);
                int minor = Integer.parseInt(nextLine[Minor]);
                float rssi = Float.parseFloat(nextLine[RSSI]);
                BeaconSimulationData beaconSimulationData = new BeaconSimulationData(timeStamp, uuid, major, minor, rssi);
                beaconSimulationDataList.add(beaconSimulationData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return beaconSimulationDataList;
    }

    public static List<DummySimulationData> readDummySimulation() {
        List<DummySimulationData> dummySimulationDataList = new ArrayList<>();
        try {
            String path = Environment.getExternalStorageDirectory() + "/" + SLBS_FOLDER_NAME +  "/" + DUMMY_SIMULATION_FILE_NAME;
            CSVReader csvReader = new CSVReader(new FileReader(path));
            String[] nextLine;
            int MAP_ID = 0, BRANCH_ID = 1, X = 2, Y = 3, INTERVAL = 4;
            while ((nextLine = csvReader.readNext()) != null) {
                // index 에 floor id 명이 있을 경우 각 index의 번호를 찾는다.
                if (nextLine[0].contains("map")) {
                    for (int i = 0; i < nextLine.length; ++i) {
                        if (nextLine[i].contains("map")) MAP_ID = i;
                        if (nextLine[i].contains("branch")) BRANCH_ID = i;
                        if (nextLine[i].contains("x")) X = i;
                        else if (nextLine[i].contains("y")) Y = i;
                        else if (nextLine[i].contains("interval")) INTERVAL = i;
                    }
                    if (MAP_ID < 0 || X < 0 || Y < 0 || INTERVAL < 0) break;
                    continue;
                }

                long mapId = Long.parseLong(nextLine[MAP_ID]);
                long branchId = Long.parseLong(nextLine[BRANCH_ID]);
                double x = Double.parseDouble(nextLine[X]);
                double y = Double.parseDouble(nextLine[Y]);
                int interval = Integer.parseInt(nextLine[INTERVAL]);
                DummySimulationData dummySimulationData = new DummySimulationData(mapId, branchId, x, y, interval);
                dummySimulationDataList.add(dummySimulationData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dummySimulationDataList;
    }*/
}