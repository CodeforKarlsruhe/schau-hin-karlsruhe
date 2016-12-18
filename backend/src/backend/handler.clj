(ns backend.handler
  (:require [clj-uuid :as uuid]
            [clojure.set :refer [rename-keys difference]]
            [clojure.walk :refer [keywordize-keys stringify-keys]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [postal.core :as mail]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :as middleware]
            [taoensso.faraday :as far]))

(def smtp {:host "smtp.1und1.de"
           :user "info@rezepter.eu"
           :port 587
           :tls :yes})

(defn send-mail
  [record]
  (mail/send-message smtp
                     {:from "info@rezepter.eu"
                      :to "tludwig@inovex.de"
                      :subject "Neuer Eintrag"
                      :body (str "Hallo, ein neuer Eintrag mit folgendem Inhalt wurde eingetragen:\n"
                                 record
                                 "Bis dann!")}))

(def client-opts
  {
   ;For Production, change these properties
   :access-key "<AWS_DYNAMODB_ACCESS_KEY>"
   :secret-key "<AWS_DYNAMODB_SECRET_KEY>"
   :endpoint "http://localhost:8000"})

(def create-table
  (fn [] (far/create-table client-opts :record-table
                           [:id :s]  ; Primary key named "id", (:n => number type)
                           {:throughput {:read 1 :write 1} ; Read & write capacity (units/sec)
                            :block? true ; Block thread during table creation
                            })))

(defn create-record
  [params]
  (let [id (str (uuid/v1))]
    (do (far/put-item client-opts
                      :record-table
                      (assoc params :id id))
        (send-mail params)
        {:success true
         :id      id})))

(defn get-all-records
  []
  (into () (far/scan client-opts :record-table)))

(defn get-record
  [id]
  {:status 200
   :body (far/get-item client-opts :record-table {:id id})})

(defn remove-record
  [id]
  (do (far/delete-item client-opts :record-table {:id id})
      {:status 204}))

(defroutes app-routes
           (GET "/" [] (get-all-records))
           (POST "/" {:keys [params]} (create-record params))
           (context "/:id" [id]
             (GET "/" [] (get-record id))
             (DELETE "/" [] (remove-record id)))
           (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:get :put :post :delete])
      (middleware/wrap-json-body)
      (middleware/wrap-json-params)
      (middleware/wrap-json-response)
      (wrap-defaults api-defaults)))