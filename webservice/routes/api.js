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

router.get("/Comments/:cityId/:zoneId/All",(req,res,next)=>{
	query="SELECT user.name, classificacao.id, user.email, classificacao.comentario, classificacao.classificacao, GROUP_CONCAT(categoria.nome) as categoria from classificacao INNER JOIN cidade_zona ON classificacao.idCidadeZona = cidade_zona.id INNER JOIN user ON classificacao.idUser = user.id INNER JOIN categoria_classificacao ON classificacao.id = categoria_classificacao.idClassificacao INNER JOIN categoria ON categoria.idCategoria = categoria_classificacao.idCategoria WHERE cidade_zona.idCidade='"+req.params.cityId+"' and cidade_zona.idZona = '"+req.params.zoneId+"' GROUP BY classificacao.id"

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
			if(req.params.numZones>0){
				query = "Insert into cidade_zona(idZona, idCidade) Values(0,"+req.params.cityId+")"
				for(var i = 1; i<req.params.numZones;i++){
					query += ",("+i+","+req.params.cityId+")"
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

router.post("/Comments/Update",(req,res,next)=>{

	console.log(req.body)
	query="Update classificacao SET comentario='"+req.body.comment+"', classificacao ='"+req.body.rating+"' where id="+req.body.idComment
	res.locals.connection.query(query, function (error, results, fields) {
		if (error) throw error;
		if(JSON.stringify(results) != "[]"){
			console.log(query)
			query = "Delete from categoria_classificacao where idClassificacao="+req.body.idComment
			res.locals.connection.query(query, function (error, results, fields) {
				if (error) throw error;
				if(JSON.stringify(results) != "[]"){
					console.log(query)
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
										query = "Insert Into categoria_classificacao (idCategoria,idClassificacao) Values("+results[0].idCategoria+","+req.body.idComment+")"
									}else{
										query = "Insert Into categoria_classificacao (idCategoria,idClassificacao) Values("+results[0].idCategoria+","+req.body.idComment+")"
										for(var i = 1; i<results.length;i++){
											query += ",("+results[i].idCategoria+","+req.body.idComment+")"
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

//GET ZONE INFO POPUP
router.get('/CityId/:cityId/ZoneId/:zoneId', function(req, res, next) {

	query="Select GROUP_CONCAT(joinedtable.comentario) as comentario, AVG(joinedtable.classificacao) as classificacao, GROUP_CONCAT(joinedtable.categoria) as categoria FROM (SELECT classificacao.comentario, classificacao.classificacao , GROUP_CONCAT(categoria.nome) as categoria from classificacao INNER JOIN cidade_zona ON classificacao.idCidadeZona = cidade_zona.id INNER JOIN categoria_classificacao ON classificacao.id = categoria_classificacao.idClassificacao INNER JOIN categoria ON categoria.idCategoria = categoria_classificacao.idCategoria WHERE cidade_zona.idCidade='"+req.params.cityId+"' and cidade_zona.idZona = '"+req.params.zoneId+"' GROUP By classificacao.id) as joinedtable"

	
	res.locals.connection.query(query, function (error, results, fields) {
		if (error) throw error;
		if(JSON.stringify(results) != "[]"){
			console.log(query)
			console.log(results)
			res.send(JSON.stringify({"status": 200, "error": null, "response": results}));
		}else{
			res.send(JSON.stringify({"status": 200, "error": null, "response": "Not Found"}))
		}
	});
	
});

//GET INFO ABOUT ALL ZONES


router.get('/CityInfo/:id', function(req, res, next) {

	query="SELECT idCidadeZona, cidade_zona.idZona as idCZ, comentario FROM classificacao INNER JOIN cidade_zona ON classificacao.idCidadeZona = cidade_zona.id WHERE cidade_zona.idCidade = '"+req.params.id+"'"

	
	res.locals.connection.query(query, function (error, results, fields) {
		if (error) throw error;
		if(JSON.stringify(results) != "[]"){
			console.log(query)
			console.log(results)
			res.send(JSON.stringify({"status": 200, "error": null, "response": results}));
		}else{
			res.send(JSON.stringify({"status": 200, "error": null, "response": "Not Found"}))
		}
	});
	
});

module.exports = router;

