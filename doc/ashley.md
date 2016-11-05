Best practice :

* use mapper to retreive components from entities : mapper.get(entity) : O(1) complexity.
  use of entity.getComponent(...) is Log(n) complexity and should be avoided.
  
* create component throw engine : use a pool

* 

Non official convention / implementations :

* Add a static mapper for all your component :
  public static final ComponentMapper<YourComponent> components = ComponentMapper.getFor(YourComponent.class);