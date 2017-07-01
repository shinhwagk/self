function del(id){
  const msg = "delete: " + id +" ?"
  if (confirm(msg) === true){
    console.info(jsRoutes.HomeController.del(id).url)
  }
}