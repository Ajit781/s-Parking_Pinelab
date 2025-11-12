package utilities.constants;

/**
 * Created by vyomahp on 6/19/2018.
 */

/*
public class JsonUtil {


    public static String toJson(String device_id) {

        try {

            ALePHModel datamodel = ALePHModel.getInstance();
            */
/*PersonDetailsModel personDetailsModel=new PersonDetailsModel();*//*

            JSONObject jsonObj = new JSONObject();
            jsonObj.put("adhaarNo", datamodel.personDetailsModel.getAdhaarNo());
            jsonObj.put("deviceNo", device_id);
            jsonObj.put("name", datamodel.personDetailsModel.getName());
            jsonObj.put("dob", datamodel.personDetailsModel.getDob());
            jsonObj.put("gender", datamodel.personDetailsModel.getGender());

            JSONObject jsonAdd = new JSONObject();

            jsonAdd.put("id", UUID.randomUUID().toString());
            jsonAdd.put("houseNoOrVillage", datamodel.personDetailsModel.getAddress().getHouseNoOrVillage());
            jsonAdd.put("locationOrPO", datamodel.personDetailsModel.getAddress().getLocationOrPO());
            jsonAdd.put("dist", datamodel.personDetailsModel.getAddress().getDist());
            jsonAdd.put("pinCode", datamodel.personDetailsModel.getAddress().getPinCode());
            jsonAdd.put("state", datamodel.personDetailsModel.getAddress().getState());
            jsonObj.put("address", jsonAdd);


            if (datamodel.haredityRecylerModelBeenArrayList.size() > 0) {
                JSONArray jsonharedityArray = new JSONArray();
                for (int i = 0; i < datamodel.haredityRecylerModelBeenArrayList.size(); i++) {

                    JSONObject jsonObjectheredity = new JSONObject();
                    jsonObjectheredity.put("id", datamodel.haredityRecylerModelBeenArrayList.get(i).getId());
                    jsonObjectheredity.put("desease", datamodel.haredityRecylerModelBeenArrayList.get(i).getHariditical_question());
                    jsonObjectheredity.put("heredityDiseaseFromPerson", datamodel.haredityRecylerModelBeenArrayList.get(i).getHariditical_comes_from());
                    jsonharedityArray.put(jsonObjectheredity);
                }
                jsonObj.put("heridityDisease", jsonharedityArray);

            }
            if (datamodel.allergyRecylerModelsArrayList.size() > 0) {
                JSONArray jsonallegyArray = new JSONArray();
                for (int i = 0; i < datamodel.allergyRecylerModelsArrayList.size(); i++) {

                    JSONObject jsonObjectallergy = new JSONObject();
                    jsonObjectallergy.put("id", datamodel.allergyRecylerModelsArrayList.get(i).getId());
                    jsonObjectallergy.put("allergyType", datamodel.allergyRecylerModelsArrayList.get(i).getAllergy_name());
                    jsonallegyArray.put(jsonObjectallergy);
                }
                jsonObj.put("allergyTypes", jsonallegyArray);

            }

            return jsonObj.toString();
        } catch (Exception e) {

        }

        return null;

    }

    public static String encounterJson(String patientId,String observation) {

        try {
            ALePHModel datamodel = ALePHModel.getInstance();
            JSONObject jsonEncounter = new JSONObject();
            jsonEncounter.put("patientId", patientId);
            jsonEncounter.put("createdBy", "KIOSK");

            jsonEncounter.put("story", datamodel.encounter.getStory());

            if (datamodel.issuesBeenArrayList.size() > 0) {
                JSONArray jsonIssueArray = new JSONArray();
                for (int i = 0; i < datamodel.issuesBeenArrayList.size(); i++) {
                    JSONObject jsonIssue = new JSONObject();
                    jsonIssue.put("id", UUID.randomUUID().toString());
                    jsonIssue.put("issue", datamodel.issuesBeenArrayList.get(i).getIssue());
                    jsonIssue.put("details", datamodel.issuesBeenArrayList.get(i).getDetails());
                    jsonIssueArray.put(jsonIssue);
                }
                jsonEncounter.put("issues", jsonIssueArray);
            }

            if (datamodel.symtompsArrayList.size() > 0) {
                JSONArray jsonSymptomArray = new JSONArray();
                for (int j = 0; j < datamodel.symtompsArrayList.size(); j++) {
                    JSONObject jsonSymptom = new JSONObject();
                    jsonSymptom.put("id", UUID.randomUUID().toString());
                    // jsonSymptom.put("symptomType",datamodel.symtompsArrayList.get(j).getSymyompType());
                    JSONObject jsonSymptomType = new JSONObject();
                    jsonSymptomType.put("id", datamodel.symtompsArrayList.get(j).getSymyompTypes().getId());
                    jsonSymptomType.put("symtompType", datamodel.symtompsArrayList.get(j).getSymyompTypes().getStmptomType());
                    jsonSymptom.put("symptomTypes", jsonSymptomType);

                    JSONObject jsonFeature = new JSONObject();
                    jsonFeature.put("id", UUID.randomUUID().toString());
                    jsonFeature.put("clinicalDescription", datamodel.symtompsArrayList.get(j).getFeatures().getClinicalDescription());
                    //jsonFeature.put("severity",datamodel.symtompsArrayList.get(j).getFeatures().getSeverity());
                    JSONObject jsonSeverity = new JSONObject();
                    jsonSeverity.put("id",datamodel.symtompsArrayList.get(j).getFeatures().getSeverity().getId());
                    jsonSeverity.put("name",datamodel.symtompsArrayList.get(j).getFeatures().getSeverity().getName());
                    jsonFeature.put("severity",jsonSeverity);
                   // jsonFeature.put("intesityDegree", datamodel.symtompsArrayList.get(j).getFeatures().getIntesityDegree());
                    JSONObject jsonIntensityDegree=new JSONObject();
                    jsonIntensityDegree.put("id",datamodel.symtompsArrayList.get(j).getFeatures().getIntesityDegree().getId());
                    jsonIntensityDegree.put("intensityDegree",datamodel.symtompsArrayList.get(j).getFeatures().getIntesityDegree().getIntensityDegree());
                    jsonFeature.put("intesityDegree",jsonIntensityDegree);
                    jsonFeature.put("duration", datamodel.symtompsArrayList.get(j).getFeatures().getDuration());
                    jsonFeature.put("character", datamodel.symtompsArrayList.get(j).getFeatures().getCharacter());
                   // jsonFeature.put("veriation", datamodel.symtompsArrayList.get(j).getFeatures().getVariations());
                    JSONObject jsonVeriation=new JSONObject();
                    jsonVeriation.put("id",datamodel.symtompsArrayList.get(j).getFeatures().getVariations().getId());
                    jsonVeriation.put("veriation",datamodel.symtompsArrayList.get(j).getFeatures().getVariations().getVariation());
                    jsonFeature.put("variations",jsonVeriation);
                    if (datamodel.symtompsArrayList.get(j).getFeatures().getPercipitingFactor().size() > 0) {
                        JSONArray jsonPrecipitatingArray = new JSONArray();
                        ArrayList<String> stringArrayList = new ArrayList<String>();
                        for (int k = 0; k < datamodel.symtompsArrayList.get(j).getFeatures().getPercipitingFactor().size(); k++) {
                            JSONObject jsonPrecipitatingObject = new JSONObject();
                            stringArrayList.add(datamodel.symtompsArrayList.get(j).getFeatures().getPercipitingFactor().get(k).getFactors());
                        }
                        jsonPrecipitatingArray.put(stringArrayList);
                        jsonFeature.put("precipitatingFactor", jsonPrecipitatingArray);
                    }


                    if (datamodel.symtompsArrayList.get(j).getFeatures().getModificationFactorseChildren().size() > 0) {
                        JSONArray jsonModificationArray = new JSONArray();
                        for (int k = 0; k < datamodel.symtompsArrayList.get(j).getFeatures().getModificationFactorseChildren().size(); k++) {
                            JSONObject jsonModificationObject = new JSONObject();
                            jsonModificationObject.put("id", UUID.randomUUID().toString());
                            jsonModificationObject.put("factors", datamodel.symtompsArrayList.get(j).getFeatures().getModificationFactorseChildren().get(k).getModification_factors());
                            // jsonModificationObject.put("change",datamodel.symtompsArrayList.get(j).getFeatures().getModificationFactorseChildren().get(k).getModification_change_spinner());
                            JSONObject jsonChangeRecycleModification = new JSONObject();
                            jsonChangeRecycleModification.put("id", datamodel.symtompsArrayList.get(j).getFeatures().getModificationFactorseChildren().get(k).getModification_change_spinner().getId());
                            jsonChangeRecycleModification.put("change", datamodel.symtompsArrayList.get(j).getFeatures().getModificationFactorseChildren().get(k).getModification_change_spinner().getChange());
                            jsonModificationObject.put("change", jsonChangeRecycleModification);
                            jsonModificationObject.put("details", datamodel.symtompsArrayList.get(j).getFeatures().getModificationFactorseChildren().get(k).getModification_details());


                            jsonModificationArray.put(jsonModificationObject);

                        }
                        jsonFeature.put("modificationFactor", jsonModificationArray);
                    }
                    jsonSymptom.put("features", jsonFeature);
                    jsonSymptomArray.put(jsonSymptom);


                }
                jsonEncounter.put("symtomps", jsonSymptomArray);
            }

            Gson gson = new Gson();
            */
/*JsonParser jsonParser = new JsonParser();
            JsonObject jsonObjectobservation = (JsonObject)jsonParser.parse(observation);
            Assert.assertNotNull(jsonObjectobservation);
            jsonEncounter.put("observations",jsonObjectobservation);*//*

            try {

                JSONObject jsonObjectobservation = new JSONObject(observation);
                jsonEncounter.put("observations",jsonObjectobservation);


                Log.d("My App", jsonObjectobservation.toString());


            } catch (Throwable tx) {
                Log.e("My App", "Could not parse malformed JSON: \"" + observation + "\"");
            }

            Log.e("enncounter json", jsonEncounter.toString());

            return jsonEncounter.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String loginJson(String deviceid, String location, String locationlatlng, String language) {

        try {
            ALePHModel datamodel = ALePHModel.getInstance();
            JSONObject jsonLogin = new JSONObject();

            jsonLogin.put("deviceNo", deviceid);
            jsonLogin.put("deviveLocation", location);

            JSONObject loglocation= new JSONObject();

            try {

                JSONObject jsoncoordinate = new JSONObject(locationlatlng);
                jsonLogin.put("location", jsoncoordinate);


                Log.d("My App", jsoncoordinate.toString());


            } catch (Throwable tx) {
                Log.e("My App", "Could not parse malformed JSON: \"" + locationlatlng + "\"");
            }



            */
/*jsonCoordinate.put("latitude", lat);
            jsonCoordinate.put("longitude", lng);*//*


            jsonLogin.put("language", language);

            Log.e("Login resp",jsonLogin.toString());

            return jsonLogin.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
*/
