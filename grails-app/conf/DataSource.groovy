dataSource {
  pooled = true
  driverClassName = "org.h2.Driver"
  username = "sa"
  password = ""
}
hibernate {
  cache.use_second_level_cache = true
  cache.use_query_cache = true
  cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}
// environment specific settings
environments {
  development {
    dataSource {
      pooled = true
      dbCreate = "update"
      url = "jdbc:mysql://localhost:3306/openmessenger?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8"
      driverClassName = "com.mysql.jdbc.Driver"
      dialect = "org.hibernate.dialect.MySQLInnoDBDialect"
      username = ""
      password = ""
      properties {
        validationQuery = 'select 1'
      }
    }
  }
  test {
    dataSource {
      dbCreate = "create-drop"
      url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
    }
  }
  production {
    dataSource {
      //messenger.opendream.org
      pooled = true
      dbCreate = "update"
      url = "jdbc:mysql://localhost:3306/openmessenger?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8"
      driverClassName = "com.mysql.jdbc.Driver"
      dialect = "org.hibernate.dialect.MySQLInnoDBDialect"
      username = ""
      password = ""
      properties {
        validationQuery = 'select 1'
      }
    }
  }
}
