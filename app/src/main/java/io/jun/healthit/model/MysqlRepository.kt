package io.jun.healthit.model

//import okhttp3.*

class MysqlRepository {

    /*fun getNutritions(chainsName:String): MutableLiveData<List<Menu>> {
        val list : MutableLiveData<List<Menu>> by lazy { MutableLiveData<List<Menu>>() }
        val menuList = ArrayList<Menu>()

        val url = URL("http://jisopjps1515.cafe24.com/nutrition_facts.php")
        val postBody = FormBody.Builder()
            .add("chains", chainsName)
            .build()
        val request = Request.Builder().url(url).post(postBody).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {

                val jsonStr = response.body()!!.string()

                val TAG_JSON = "result"
                val TAG_NAME = "name"
                val TAG_SERV = "serv"
                val TAG_CALORIE = "calorie"
                val TAG_CARBO = "carbo"
                val TAG_SUGAR = "sugar"
                val TAG_PROTEIN = "protein"
                val TAG_FAT = "fat"
                val TAG_SODIUM = "sodium"

                val jsonObject = JSONObject(jsonStr)
                val jsonArray: JSONArray = jsonObject.getJSONArray(TAG_JSON)
                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    val name = item.getString(TAG_NAME)
                    val serv = item.getString(TAG_SERV)
                    val calorie = item.getInt(TAG_CALORIE)
                    val carbo = item.getInt(TAG_CARBO)
                    val sugar = item.getString(TAG_SUGAR)
                    val protein = item.getInt(TAG_PROTEIN)
                    val fat = item.getInt(TAG_FAT)
                    val sodium = item.getString(TAG_SODIUM)
                    val menu = Menu(name, serv, calorie, carbo, sugar, protein, fat, sodium)

                    menuList.add(menu)
                }
                list.postValue(menuList)
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("OkHttp error : ", "Failed to execute request!")
                list.postValue(null)
            }
        })

        return list
    }

    fun getChains(foodType:String): MutableLiveData<List<Chains>> {
        val list : MutableLiveData<List<Chains>> by lazy { MutableLiveData<List<Chains>>() }
        val chainsList = ArrayList<Chains>()

        val url = URL("http://jisopjps1515.cafe24.com/chains.php")
        val postBody = FormBody.Builder()
            .add("foodType", foodType)
            .build()
        val request = Request.Builder()
            .url(url)
            .post(postBody)
            .build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {

                val jsonStr = response.body()!!.string()

                val TAG_JSON = "result"
                val TAG_NAME = "name"

                val jsonObject = JSONObject(jsonStr)
                val jsonArray: JSONArray = jsonObject.getJSONArray(TAG_JSON)
                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    val name = item.getString(TAG_NAME)

                    val chains = Chains(name)

                    chainsList.add(chains)
                }
                list.postValue(chainsList)
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("OkHttp error : ", "Failed to execute request!")
                list.postValue(null)
            }
        })

        return list
    }

    fun getRoutine(routineType: String): MutableLiveData<List<Routine>> {
        val list : MutableLiveData<List<Routine>> by lazy { MutableLiveData<List<Routine>>() }
        val routineList = ArrayList<Routine>()

        Log.e("wegegwewgeg", "sdf : $routineType")

        val url = URL("http://jisopjps1515.cafe24.com/routine.php")
        val postBody = FormBody.Builder()
            .add("routineType", routineType)
            .build()
        val request = Request.Builder()
            .url(url)
            .post(postBody)
            .build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {

                val jsonStr = response.body()!!.string()

                val TAG_JSON = "result"
                val TAG_TITLE = "title"
                val TAG_CONTENT = "content"
                val TAG_NOTICE = "notice"
                val TAG_LINK = "link"

                val jsonObject = JSONObject(jsonStr)
                val jsonArray: JSONArray = jsonObject.getJSONArray(TAG_JSON)
                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    val title = item.getString(TAG_TITLE)
                    val content = item.getString(TAG_CONTENT)
                    val notice = item.getString(TAG_NOTICE)
                    val link = item.getString(TAG_LINK)

                    *//*val routine = Routine(false, title, content, notice, link)

                    routineList.add(routine)*//*
                }
                list.postValue(routineList)
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("OkHttp error : ", "Failed to execute request!")
                list.postValue(null)
            }
        })

        return list
    }*/
}