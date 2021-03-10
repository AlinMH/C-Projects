package reader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ConfigReader {
    public String pathToFile;
    public Long numFloors;
    public Long numElevators;
    public Long capacity;
    public Long tDoors;
    public Long tClose;
    public Long tTransit;
    public Long tSlow;
    public Long tAccel;
    public Long tFast;
    public Scenario scenario;


    public ConfigReader(String pathToFile) {
        this.pathToFile = pathToFile;
    }

    public void parseConfigFile(String scenarioName) {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(pathToFile));
            JSONObject jsonObject = (JSONObject) obj;

            numFloors = (Long) jsonObject.get("F");
            numElevators = (Long) jsonObject.get("E");
            capacity = (Long) jsonObject.get("C");

            JSONObject dynamic = (JSONObject) jsonObject.get("dynamic");
            JSONObject jsonScenarios = (JSONObject) jsonObject.get("scenarios");

            tDoors = (Long) dynamic.get("Tdoors");
            tClose = (Long) dynamic.get("Tclose");
            tTransit = (Long) dynamic.get("Ttransit");
            tSlow = (Long) dynamic.get("Tslow");
            tAccel = (Long) dynamic.get("Taccel");
            tFast = (Long) dynamic.get("Tfast");

            List<JSONObject> generators = (List<JSONObject>) jsonScenarios.get(scenarioName);
            List<Generator> generatorList = new ArrayList<>();
            for (JSONObject jsonObj : generators) {
                Long tFrom = (Long) jsonObj.get("Tfrom");
                Long tTo = (Long) jsonObj.get("Tto");
                Long oFrom = (Long) jsonObj.get("Ofrom");
                Long oTo = (Long) jsonObj.get("Oto");
                Long dFrom = (Long) jsonObj.get("Dfrom");
                Long dTo = (Long) jsonObj.get("Dto");
                Long period = (Long) jsonObj.get("Period");
                Generator generator = new Generator(tFrom, tTo, oFrom, oTo, dFrom, dTo, period);
                generatorList.add(generator);
            }
            scenario = new Scenario(generatorList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        ConfigReader configReader = new ConfigReader("tests/7floor.json");
        configReader.parseConfigFile("normal");
    }
}
