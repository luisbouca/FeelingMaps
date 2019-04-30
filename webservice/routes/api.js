var express = require('express');
var router = express.Router();

/* GET users listing. */
router.get('/Level0/:level0/Level1/:level1/Level2/:level2/Level3/:level3/Level4/:level4', function(req, res, next) {
	query="SELECT * from coordinates_geo where name_0='"+req.params.level0+"' AND name_1='"+req.params.level1+"' AND name_2='"+req.params.level2+"' AND name_3 Like '%"+req.params.level3+"%' AND name_4 Like '%"+req.params.level4+"%'"
	res.locals.connection.query(query, function (error, results, fields) {
		if (error) throw error;
		if(JSON.stringify(results) != "[]"){
			console.log(query)
			res.send(JSON.stringify({"status": 200, "error": null, "response": results}));
		}else{
			res.send(JSON.stringify({"status": 200, "error": null, "response": "Not Found"}))
		}
	});
});
router.get('/Level0/:level0/Level1/:level1/Level2/:level2/Level3/:level3', function(req, res, next) {
	query="SELECT * from coordinates_geo where name_0='"+req.params.level0+"' AND name_1='"+req.params.level1+"' AND name_2='"+req.params.level2+"' AND name_3 Like '%"+req.params.level3+"%'"
	res.locals.connection.query(query, function (error, results, fields) {
		if (error) throw error;
		if(JSON.stringify(results) != "[]"){
			console.log(query)
			res.send(JSON.stringify({"status": 200, "error": null, "response": results}));
		}else{
			res.send(JSON.stringify({"status": 200, "error": null, "response": "Not Found"}))
		}
	});
});
router.get('/Level0/:level0/Level1/:level1/Level2/:level2/', function(req, res, next) {
	query="SELECT * from coordinates_geo where name_0='"+req.params.level0+"' AND name_1='"+req.params.level1+"' AND name_2='"+req.params.level2+"'"
	res.locals.connection.query(query, function (error, results, fields) {
		if (error) throw error;
		if(JSON.stringify(results) != "[]"){
			console.log(query)
			res.send(JSON.stringify({"status": 200, "error": null, "response": results}));
		}else{
			res.send(JSON.stringify({"status": 200, "error": null, "response": "Not Found"}))
		}
	});
});
router.get('/Level0/:level0/Level1/:level1', function(req, res, next) {
	query="SELECT * from coordinates_geo where name_0='"+req.params.level0+"' AND name_1='"+req.params.level1+"'"
	res.locals.connection.query(query, function (error, results, fields) {
		if (error) throw error;
		if(JSON.stringify(results) != "[]"){
			console.log(query)
			res.send(JSON.stringify({"status": 200, "error": null, "response": results}));
		}else{
			res.send(JSON.stringify({"status": 200, "error": null, "response": "Not Found"}))
		}
	});
});

router.get("/Comments/:cityid/:id/All",(req,res,next)=>{
	query="SELECT * from classificacao where idCidadeZona=(Select id from cidade_zona where idCidade='"+req.params.cityid+"' and idZona='"+req.params.id+"')"
	res.locals.connection.query(query, function (error, results, fields) {
		if (error) throw error;
		if(JSON.stringify(results) != "[]"){
			console.log(query)
			res.send(JSON.stringify({"status": 200, "error": null, "response": results}));
		}else{
			res.send(JSON.stringify({"status": 200, "error": null, "response": "Not Found"}))
		}
	});
})

router.post("/Comments/",(req,res,next)=>{

	console.log(req.body)
	query="Insert Into classificacao (idCidadeZona,idUser,comentario,classificacao) Values((Select id from cidade_zona where idCidade="+req.body.cityId+" and idzona="+req.body.id+"),"+req.body.user+","+req.body.comment+","+req.body.rating+")"
	res.locals.connection.query(query, function (error, results, fields) {
		if (error) throw error;
		if(JSON.stringify(results) != "[]"){
			console.log(query)
			res.send(JSON.stringify({"status": 200, "error": null, "response": results}));
		}else{
			res.send(JSON.stringify({"status": 200, "error": null, "response": "Not Found"}))
		}
	});
})


module.exports = router;