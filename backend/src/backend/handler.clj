(ns backend.handler
  (:require [clj-uuid :as uuid]
            [clojure.set :refer [rename-keys difference]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :as middleware]
            [taoensso.faraday :as far]

            ))
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

(def record-must-keys  (list :email))
(def record-could-keys (list :description))

(defn check-params
  [params]
  (and (every? true? (map #(contains? params %) record-must-keys))
       (empty? (difference (set (keys params))
                           (set (concat record-could-keys record-must-keys))))))

(def invalid-json-response
  {:status 400
   :body {:error "Invalid JSON"}})

(defn create-record
  [params]
  (let [id (uuid/v1)]
    (do (far/put-item client-opts
                      :record-table
                      (assoc params :id id))
        {:success true
         :id      id})))

(defn get-all-records
  []
  (far/scan client-opts :record-table))

(defn get-record
  [id]
  (far/get-item client-opts :record-table {:id id}))

(defn remove-record
  [id]
  (far/delete-item client-opts :record-table {:id id}))

(defn update-expressions
  []
  (str "SET = "))

(defn update-record
  [id params]
  (if (check-params params)
    (far/update-item client-opts
                     :record-table
                     (:id id)
                     {:update-expr (update-expressions)
                      :expr-attr-vals params
                      })
    invalid-json-response))


(defroutes app-routes
           (GET "/" [] (get-all-records))
           (POST "/" {:keys [params]} (create-record params))
           (context "/:id" [id]
             (GET "/" [] (get-record id))
             (POST "/" {:keys [params]} (update-record id params))
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
