/*
Format: Electric Flow DSL
File: createPipeline.groovy
Description: Create release pipeline

*/

def stages = "$[stages]".split(",") // comma separated list
def pipelineName = "$[release]"

project "Default", {
	pipeline pipelineName, {
		stages.eachWithIndex { st, index ->
			stage st,{
				task "Batch Deploy",
					taskType: "DEPLOYER"
				task "Update ticket",
					taskType: 'PROCEDURE',
					subproject: "$[/myProject]",
					subprocedure: "UpdateTicket"
				task "Test Automation",
					taskType: 'PROCEDURE',
					subproject: "$[/myProject]",
					subprocedure: "SeleniumTests"
				if (index == 0) {
					task "Test Automation"
					task "Manual Validation",
						taskType: "MANUAL",
						approvers: "quincy"
					}
				if (index > 0) task "Entry gate approval",  // Don't create a gate for first stage
					taskType: 'APPROVAL',
					approver: ['admin'],
					gateType: 'PRE',
					notificationTemplate: 'ec_default_pipeline_notification_template'
			} // stage
		} // Each stage
	} // Pipeline
} // Project