{:dev-env-vars {:env
                {:mongo-host "127.0.0.1" 
                 :mongo-port 27017
                 :users-db "db"
                 :default-admin "email@mail.com"
                 :client-id "ID"
                 :client-secret "SECRET"
                 :oauth-domain "http://localhost:3000"
                 :oauth-callback "/oauth2callback"
                 :auth-url "https://accounts.google.com/o/oauth2/auth"
                 :token-url "https://accounts.google.com/o/oauth2/token"}}

:test-env-vars {:env
                {:mongo-host "127.0.0.1"
                 :mongo-port 27017
                 :users-db "pasmo-users-test"}}}

