/**
 * Copyright (c) 2009
 * Philipp Giese, Sven Wagner-Boysen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.hpi.bpmn2_0.factory;

import org.oryxeditor.server.diagram.Shape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.model.FormalExpression;
import de.hpi.bpmn2_0.model.activity.Task;
import de.hpi.bpmn2_0.model.activity.misc.Operation;
import de.hpi.bpmn2_0.model.data_object.Message;
import de.hpi.bpmn2_0.model.diagram.EventShape;
import de.hpi.bpmn2_0.model.event.CompensateEventDefinition;
import de.hpi.bpmn2_0.model.event.ConditionalEventDefinition;
import de.hpi.bpmn2_0.model.event.ErrorEventDefinition;
import de.hpi.bpmn2_0.model.event.Escalation;
import de.hpi.bpmn2_0.model.event.EscalationEventDefinition;
import de.hpi.bpmn2_0.model.event.MessageEventDefinition;
import de.hpi.bpmn2_0.model.event.SignalEventDefinition;
import de.hpi.bpmn2_0.model.event.StartEvent;
import de.hpi.bpmn2_0.model.event.TimerEventDefinition;
import de.hpi.bpmn2_0.model.misc.Error;
import de.hpi.bpmn2_0.model.misc.Signal;
import de.hpi.diagram.SignavioUUID;

/**
 * The factory for start events
 * 
 * @author Sven Wagner-Boysen
 *
 */
@StencilId({
	"StartNoneEvent",
	"StartTimerEvent",
	"StartEscalationEvent",
	"StartConditionalEvent",
	"StartErrorEvent",
	"StartCompensationEvent",
	"StartSignalEvent",
	"StartMultipleEvent",
	"StartParallelMultipleEvent",
	"StartMessageEvent"
})
public class StartEventFactory extends AbstractBpmnFactory {

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createBpmnElement(org.oryxeditor.server.diagram.Shape)
	 */
	@Override
	public BPMNElement createBpmnElement(Shape shape, BPMNElement parent) throws BpmnConverterException {
		EventShape eventShape = this.createDiagramElement(shape);
		StartEvent startEvent = this.createProcessElement(shape);
		
		/* Set Reference from shape to process element */
		eventShape.setEventRef(startEvent);
		
		return new BPMNElement(eventShape, startEvent, shape.getResourceId());
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createDiagramElement(org.oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected EventShape createDiagramElement(Shape shape) {
		EventShape eventShape = new EventShape();
		this.setVisualAttributes(eventShape, shape);
		return eventShape;
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected StartEvent createProcessElement(Shape shape) throws BpmnConverterException {
		StartEvent event;
		try {
			event = (StartEvent) this.invokeCreatorMethod(shape);
		} catch (Exception e) {
			/* Wrap exceptions into specific BPMNConverterException */
			throw new BpmnConverterException(
					"Error while creating the process element of "
							+ shape.getStencilId(), e);
		}
		event.setId(shape.getResourceId());
		event.setName(shape.getProperty("name"));
		
		/* Interrupting property */
		String interrupting = shape.getProperty("isinterrupting");
		if(interrupting != null)
			event.setIsInterrupting(!interrupting.equalsIgnoreCase("false"));
		
		return event;
	}
	
	/* Creator methods for different event definitions */
	
	@StencilId("StartMessageEvent")
	protected StartEvent createStartMessageEvent(Shape shape) {
		StartEvent event = new StartEvent();
		MessageEventDefinition msgEvDef = new MessageEventDefinition();
		
		Message message = new Message();
		Operation operation = new Operation();
		
		/* Message name */
		String messageName = shape.getProperty("messagename");
		if(messageName != null && !messageName.isEmpty()) {
			message.setName(messageName);
		}
		
		/* Operation name */
		String operationName = shape.getProperty("operationname");
		if(operationName != null && !operationName.isEmpty()) {
			operation.setName(operationName);
		}
		
		msgEvDef.setMessageRef(message);
		msgEvDef.setOperationRef(operation);
		event.getEventDefinition().add(msgEvDef);
		
		return event;
	}
	
	@StencilId("StartNoneEvent")
	protected StartEvent createStartNoneEvent(Shape shape) {
		StartEvent event = new StartEvent();
		
		return event;
	}
	
	@StencilId("StartTimerEvent")
	protected StartEvent createStartTimerEvent(Shape shape) {
		StartEvent event = new StartEvent();
		TimerEventDefinition evDef = new TimerEventDefinition();
		
		/* Time Date */
		String timeDate = shape.getProperty("timedate");
		if(timeDate != null && !timeDate.isEmpty()) {
			FormalExpression expr = new FormalExpression(timeDate);
			evDef.setTimeDate(expr);
		}
		
		/* Time Cycle */
		String timeCycle = shape.getProperty("timecycle");
		if(timeCycle != null && !timeCycle.isEmpty()) {
			FormalExpression expr = new FormalExpression(timeCycle);
			evDef.setTimeCycle(expr);
		}
		
		/* Time Duration */
		String timeDuration = shape.getProperty("timeduration");
		if(timeDuration != null && !timeDuration.isEmpty()) {
			FormalExpression expr = new FormalExpression(timeDuration);
			evDef.setTimeDuration(expr);
		}
		
		event.getEventDefinition().add(evDef);
		
		return event;
	}
	
	@StencilId("StartEscalationEvent")
	protected StartEvent createStartEscalationEvent(Shape shape) {
		StartEvent event = new StartEvent();
		EscalationEventDefinition evDef = new EscalationEventDefinition();
		
		Escalation escalation = new Escalation();
		
		/* Escalation name */
		String escalationName = shape.getProperty("escalationname");
		if(escalationName != null && !escalationName.isEmpty()) {
			escalation.setName(escalationName);
		}
		
		/* Escalation code */
		String escalationCode = shape.getProperty("escalationcode");
		if(escalationCode != null && !escalationCode.isEmpty()) {
			escalation.setEscalationCode(escalationCode);
		}
		
		evDef.setEscalationRef(escalation);
		event.getEventDefinition().add(evDef);
		
		return event;
	}
	
	@StencilId("StartConditionalEvent")
	protected StartEvent createStartConditionalEvent(Shape shape) {
		StartEvent event = new StartEvent();
		ConditionalEventDefinition evDef = new ConditionalEventDefinition();
		
		/* Set condition attribute as FormalExpression */
		String condition = shape.getProperty("condition");
		if(condition != null && !condition.isEmpty())
			evDef.setCondition(new FormalExpression(condition));
		
		event.getEventDefinition().add(evDef);
		
		return event;
	}
	
	@StencilId("StartErrorEvent")
	protected StartEvent createStartErrorEvent(Shape shape) {
		StartEvent event = new StartEvent();
		ErrorEventDefinition evDef = new ErrorEventDefinition();
		
		Error error = new Error();
		
		/* Error name */
		String errorName = shape.getProperty("errorname");
		if(errorName != null && !errorName.isEmpty()) {
			error.setName(errorName);
		}
		
		/* Error code */
		String errorCode = shape.getProperty("errorcode");
		if(errorCode != null && !errorCode.isEmpty()) {
			error.setErrorCode(errorCode);
		}
		
		evDef.setError(error);
		event.getEventDefinition().add(evDef);
		
		return event;
	}
	
	@StencilId("StartCompensationEvent")
	protected StartEvent createStartCompensateEvent(Shape shape) {
		StartEvent event = new StartEvent();
		CompensateEventDefinition evDef = new CompensateEventDefinition();
		
		/* Activity Reference */
		String activityRef = shape.getProperty("activityref");
		if(activityRef != null && !activityRef.isEmpty()) {
			Task taskRef = new Task();
			taskRef.setId(activityRef);
			evDef.setActivityRef(taskRef);
		}
		
		/* Wait for Completion */
		String waitForCompletion = shape.getProperty("waitforcompletion");
		if(waitForCompletion != null && waitForCompletion.equals("false")) {
			evDef.setWaitForCompletion(false);
		} else {
			evDef.setWaitForCompletion(true);
		}
		
		event.getEventDefinition().add(evDef);
		
		return event;
	}
	
	@StencilId("StartSignalEvent")
	protected StartEvent createStartSignalEvent(Shape shape) {
		StartEvent event = new StartEvent();
		SignalEventDefinition evDef = new SignalEventDefinition();
		
		Signal signal = new Signal();
		
		/* Signal ID */
		signal.setId(SignavioUUID.generate());
		
		/* Signal name */
		String signalName = shape.getProperty("signalname");
		if(signalName != null && !signalName.isEmpty()) {
			signal.setName(signalName);
		}
		
		evDef.setSignalRef(signal);
		event.getEventDefinition().add(evDef);
		
		return event;
	}
	
	@StencilId("StartMultipleEvent")
	protected StartEvent createStartMultipleEvent(Shape shape) {
		StartEvent event = new StartEvent();
		
		return event;
	}
	
	@StencilId("StartParallelMultipleEvent")
	protected StartEvent createStartParallelMultipleEvent(Shape shape) {
		StartEvent event = new StartEvent();
		event.setParallelMultiple(true);
		
		return event;
	}

}
