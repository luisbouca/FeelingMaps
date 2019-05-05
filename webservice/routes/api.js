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
});

router.get('/Email/:email/Password/:password', function(req, res, next) {
	query="SELECT CASE WHEN EXISTS(SELECT * FROM user WHERE email like '"+req.params.email+"'  and password like '"+req.params.password+"') THEN 1 ELSE 0 END AS 'authentication'"
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
router.get('/Email/:email/Name/:name', function(req, res, next) {
	query="SELECT CASE WHEN EXISTS(SELECT * FROM user WHERE email like '"+req.params.email+"' OR name like '"+req.params.name+"') THEN 1 ELSE 0 END AS 'registered'"
	res.locals.connection.query(query, function (error, results, fields) {
		if (error) throw error;
		if(JSON.stringify(results) != "[]"){
			console.log(query)
			res.send(JSON.stringify({"status": 200, "error": null, "response": results}));
		}else{
			res.send(JSON.stringify({"status": 200, "error": null, "response": "Not Found"}))
		}
	})
})

router.get("/City/:cityId/:numZones",(req,res,next)=>{
	query = "Select * from cidade_zona where idZona='0' and idCidade='"+req.params.cityId+"'";
	res.locals.connection.query(query, function (error, results, fields) {
		if (error) throw error;
		if(JSON.stringify(results) != "[]"){
			console.log(query)
			res.send(JSON.stringify({"status": 200, "error": null, "response": results}));
		}else{
			console.log(results)
			query = "Insert into cidade_zona(idZona, idCidade) Values(0,"+req.params.cityId+")"
			for(var i = 1; i<req.params.numZones;i++){
				query += ",("+i+","+req.params.cityId+")"
			}
			res.locals.connection.query(query, function (error, results, fields) {
				if (error) throw error;
				if(JSON.stringify(results) != "[]"){
					console.log(query)
					res.send(JSON.stringify({"status": 200, "error": null, "response": results}));
				}else{
					res.send(JSON.stringify({"status": 200, "error": null, "response": "Not Found"}))
				}
			})
		}
	})
})
router.post("/Comments/",(req,res,next)=>{

	console.log(req.body)
	query="Insert Into classificacao (idCidadeZona,idUser,comentario,classificacao) Values((Select id from cidade_zona where idCidade="+req.body.cityId+" and idzona="+req.body.id+"),(Select id from user where email LIKE '"+decodeURIComponent(req.body.email)+"'),'"+req.body.comment+"','"+req.body.rating+"')"
	res.locals.connection.query(query, function (error, results, fields) {
		if (error) throw error;
		if(JSON.stringify(results) != "[]"){
			console.log(query)
			//get classification id
			id = results.insertId
			categories = req.body.categories.split(",")
			if(categories.length>0){
				if(categories.length == 1){
					query = "Select idCategoria from categoria where nome in('"+categories[0]+"')"
				}else{
					query = "Select idCategoria from categoria where nome in('"+categories[0]+"'"
					for(var i = 1; i<categories.length;i++){
						query += ",'"+categories[i]+"'"
					}
					query += ")"
				}
				res.locals.connection.query(query, function (error, results, fields) {
					if (error) throw error;
					if(JSON.stringify(results) != "[]"){
						console.log(query)
						if(results.length>0){
							if(results.length == 1){
								query = "Insert Into categoria_classificacao (idCategoria,idClassificacao) Values("+results[0].idCategoria+","+id+")"
							}else{
								query = "Insert Into categoria_classificacao (idCategoria,idClassificacao) Values("+results[0].idCategoria+","+id+")"
								for(var i = 1; i<results.length;i++){
									query += ",("+results[i].idCategoria+","+id+")"
								}
							}
							res.locals.connection.query(query, function (error, results, fields) {
								if (error) throw error;
								if(JSON.stringify(results) != "[]"){
									console.log(query)
									res.send(JSON.stringify({"status": 200, "error": null, "response": results}));
								}else{
									res.send(JSON.stringify({"status": 200, "error": null, "response": "Not Found"}))
								}
							});
						}
						
					}else{
						res.send(JSON.stringify({"status": 200, "error": null, "response": "Not Found"}))
					}
				});
			}
		}else{
			res.send(JSON.stringify({"status": 200, "error": null, "response": "Not Found"}))
		}
	});
});

router.get('/Name/:name/Email/:email/Password/:password', function(req, res, next) {
	query="INSERT INTO user (name, email, password) VALUES('"+req.params.name+"', '"+req.params.email+"', '"+req.params.password+"')"

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

module.exports = router;