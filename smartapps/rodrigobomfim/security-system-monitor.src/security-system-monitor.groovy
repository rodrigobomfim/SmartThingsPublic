/**
 *  Security System Monitor
 *
 *  Copyright 2017 Rodrigo Bomfim
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Security System Monitor",
    namespace: "rodrigobomfim",
    author: "Rodrigo Bomfim",
    description: "Sends reminder notifications about the home security system when the location change modes.",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences 
{
	section("Perferences") {
		input "securitySystem", "capability.securitySystem", title: "Security System", required: true
	}
}

def installed() 
{
	initialize()
    
	debugTrace("Installed with settings: ${settings}")
}

def updated() 
{
	unsubscribe()
	initialize()
    
	debugTrace("Updated with settings: ${settings}")
}

def initialize() 
{
    subscribe(location, onLocationChange)
	state.lastMode = location.mode
}

//
// Location change event handler
//
def onLocationChange(event) 
{
    if (modeChanged()) 
    {   
        def message = "Security system is in the correct setting."
    	def systemStatus = getSecuritySystemStatus()
    	
        if ( (location.mode == "Away" & systemStatus != "armedAway") |
        	 (location.mode == "Night" & systemStatus != "armedStay") )
        {
        	message = "Security system is off. Did you forget to turn it on?"
      		sendPushMessage(message)
        }
        
        debugTrace(message)
    }
}

//
// Retrieve the current securty system status
//
def getSecuritySystemStatus()
{
	def currentSystemStauts = securitySystem.currentValue("securitySystemStatus")
    
    debugTrace("Current security system status is: ${currentSystemStauts}")
    
    return currentSystemStauts
}

//
// Verify is the mode has changed and updates current mode
//
def modeChanged()
{
    if (state.lastMode != location.mode) 
    {   
		debugTrace("Mode changed to ${location.mode}")
        
		state.lastMode = location.mode
    	return true
    }

    return false
}

//
// Debug messages
//
def debugTrace(String message)
{
	log.debug message
}