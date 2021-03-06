/*
    Copyright 2015 Australian National Botanic Gardens

    This file is part of NSL mapper project.

    Licensed under the Apache License, Version 2.0 (the "License"); you may not
    use this file except in compliance with the License. You may obtain a copy
    of the License at http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package au.org.biodiversity.nsl

import grails.util.Environment
import org.apache.shiro.SecurityUtils

import java.sql.Timestamp

class Match {

    String uri //the unique identifier section of the match less the resolver host e.g. name/apni/123456
    Boolean deprecated = false //if this URI is depecated and should no longer be referred to publicly
    Timestamp updatedAt
    String updatedBy

    static belongsTo = [Identifier, Host]
    static hasMany = [identifiers: Identifier, hosts: Host]
    static mappedBy = [identifiers: "identities"]

    static mapping = {
        version false
        id generator: 'native', params: [sequence: 'mapper_sequence'], defaultValue: "nextval('mapper.mapper_sequence')"
        uri index: 'identity_uri_index', unique: true
        deprecated defaultvalue: "false"
        if (Environment.current != Environment.TEST) { // test uses H2 so it doesn't understand this
            updatedAt sqlType: 'timestamp with time zone'
        }
    }

    static constraints = {
        uri unique: true
        updatedAt nullable: true
        updatedBy nullable: true
    }

    def beforeInsert() {
        updatedAt = new Timestamp(System.currentTimeMillis())
        updatedBy = SecurityUtils.subject?.getPrincipal()?.toString()
    }

    def beforeUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis())
        updatedBy = SecurityUtils.subject?.getPrincipal()?.toString()
    }

    @Override
    String toString() {
        "$id: $uri"
    }
}
