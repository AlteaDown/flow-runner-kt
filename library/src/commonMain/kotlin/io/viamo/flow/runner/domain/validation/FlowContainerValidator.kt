package io.viamo.flow.runner.domain.validation

import io.viamo.flow.runner.model.block.*

// TODO: Convert this to do validation via init()
/*

function folderPathFromSpecificationVersion(version: String): String | null {
  if (version == '1.0.0-rc1') {
    return '../../../dist/resources/validationSchema/1.0.0-rc1/'
  } else if (version == '1.0.0-rc2') {
    return '../../../dist/resources/validationSchema/1.0.0-rc2/'
  }
  return null
}

*/
/**
 * Validate a Flow Spec container and return a set of errors (if they exist).
 * This checks that the structure of the container is valid according to the flow spec.
 * It does not check that the configuration of blocks is complete and ready to run/publish the flow;
 * for this see getFlowCompletenessErrors()
 * @param container : The result of calling JSON.parse() on flow spec container json
 * @returns null if there are no errors, or a set of validation errors
 *//*

fun getFlowStructureErrors(container: IContainer, shouldValidateBlocks = true): ErrorObject<String, Record<String, any>, unknown>[] | null? {
    var flowSpecJsonSchema: any
  if (container.specification_version == '1.0.0-rc1') {
    flowSpecJsonSchema = require('../../../dist/resources/validationSchema/1.0.0-rc1/flowSpecJsonSchema.json');
  }
  else if (container.specification_version == '1.0.0-rc2') {
    flowSpecJsonSchema = require('../../../dist/resources/validationSchema/1.0.0-rc2/flowSpecJsonSchema.json');
  }
  else {
    return [
      {
        keyword: 'version',
        dataPath: '/container/specification_version',
        schemaPath: '#/properties/specification_version',
        params: [],
        propertyName: 'specification_version',
        message: 'Unsupported specification version',
      },
    ]
  }

  val ajv = new Ajv()
  // we need this to use AJV format such as 'date-time' (https://json-schema.org/draft/2019-09/json-schema-validation.html#rfc.section.7)
  ajvFormat(ajv)
  val validate = ajv.compile(flowSpecJsonSchema)
  if (!validate(container)) {
    return validate.errors
  }

  if (shouldValidateBlocks) {
    val blockSpecificErrors = checkIndividualBlocks(container)
    if (blockSpecificErrors && blockSpecificErrors.size > 0) {
      return blockSpecificErrors
    }
  }

  val missingResources = checkAllResourcesPresent(container)
  if (missingResources != null) {
    return [
      {
        keyword: 'missing',
        dataPath: '/container/resources',
        schemaPath: '#/properties/resources',
        params: [],
        propertyName: 'resources',
        message: 'Resources specified in block configurations are missing from resources: ' + missingResources.join(','),
      },
    ]
  }

  return null
}

*/
/**
 * Detailed checking of individual blocks, based on their unique jsonSchema requirements
 *//*

function checkIndividualBlocks(container: IContainer): ErrorObject<String, Record<String, any>, unknown>[] | null? {
  var errors: List<any> = []
  container.flows.forEach((flow, flowIndex) -> {
    flow.blocks.forEach((block, blockIndex) -> {
      errors = errors.concat(checkIndividualBlock(block, container, blockIndex, flowIndex))
    })
  })
  return errors
}

function checkIndividualBlock(block: IBlock, container: IContainer, blockIndex: Number, flowIndex: Number): ErrorObject<String, Record<String, any>, unknown>[] | null? {
  val schemaFileName = blockTypeToInterfaceName(block.type)
  if (schemaFileName != null) {
    val ajv = new Ajv()
    ajvFormat(ajv)
        val jsonSchema = require(folderPathFromSpecificationVersion(container.specification_version) + schemaFileName + '.json')
    val validate = ajv.compile(jsonSchema)
    if (!validate(block)) {
      return validate.errors?.map(error -> {
        error.dataPath = '/container/flows/' + flowIndex + '/blocks/' + blockIndex + error.dataPath
        return error
      })
    }
  }

  // Check that exits has at least one exit, and that the Default exit is listed last
  val exitError = checkExitsOnBlock(block)
  if (exitError != null) {
    return [
      {
        keyword: 'invalid',
        dataPath: '/container/flows/' + flowIndex + '/blocks/' + blockIndex + '/exits',
        schemaPath: '#/properties/exits',
        params: [],
        propertyName: 'exits',
        message: exitError,
      },
    ]
  }
  return []
}

function checkExitsOnBlock(block: IBlock): String | null {
  if (block.exits.size < 1) {
    return 'There must be at least one exit.'
  }
  if (block.exits[block.exits.size - 1].default != true) {
    return 'The last exit must be a default exit.'
  }
  if (
    block.exits.slice(0, -1).reduce(function (prev, current, _i) {
      return prev || current.default == true
    }, false)
  ) {
    return 'There must not be more than one default exit.'
  }

  return null
}

function blockTypeToInterfaceName(type: String): String | null {
  switch (type) {
    case 'Core.Log':
      return 'io.viamo.flow.runner.block.ILogBlock'
    case 'Core.Case':
      return 'io.viamo.flow.runner.block.ICaseBlock'
    case 'Core.RunBlock':
      return 'io.viamo.flow.runner.block.IRunFlowBlock'
    case 'Core.Output':
      return 'io.viamo.flow.runner.block.IOutputBlock'
    case 'Core.SetContactProperty':
      return 'io.viamo.flow.runner.block.ISetContactPropertyBlock'
    case 'Core.SetGroupMembership':
      return 'io.viamo.flow.runner.block.ISetGroupMembershipBlock'
    case 'ConsoleIO.Print':
      return 'io.viamo.flow.runner.block.IPrintBlock'
    case 'ConsoleIO.Read':
      return 'io.viamo.flow.runner.block.IReadBlock'
    case 'MobilePrimitives.Message':
      return 'io.viamo.flow.runner.block.IMessageBlock'
    case 'MobilePrimitives.SelectOneResponse':
      return 'io.viamo.flow.runner.block.ISelectOneResponseBlock'
    case 'MobilePrimitives.SelectManyResponses':
      return 'io.viamo.flow.runner.block.ISelectManyResponseBlock'
    case 'MobilePrimitives.NumericResponse':
      return 'io.viamo.flow.runner.block.INumericResponseBlock'
    case 'MobilePrimitives.OpenResponse':
      return 'io.viamo.flow.runner.block.IOpenResponseBlock'
    case 'SmartDevices.LocationResponse':
      return 'io.viamo.flow.runner.block.ILocationResponseBlock'
    case 'SmartDevices.PhotoResponse':
      return 'io.viamo.flow.runner.block.IPhotoResponseBlock'
    default:
      return null
  }
}

*/
/**
 * Check that all resources asked for within blocks are available in the Resources array of the container
 * @param container Flow package container
 * @returns null if all resources are available, otherwise an array of the missing resource UUIDs
 *//*

function checkAllResourcesPresent(container: IContainer): List<String> | null {
  val resourcesRequested: List<String> = []
  container.flows.forEach(flow -> {
    flow.blocks.forEach(block -> {
      if (block.type == 'MobilePrimitives.Message') {
        val b = block as IMessageBlock
        resourcesRequested.push(b.config.prompt)
      }

      if (block.type == 'MobilePrimitives.SelectOneResponse') {
        val b = block as ISelectOneResponseBlock
        if (b.config.prompt != null) {
          resourcesRequested.push(b.config.prompt)
        }
        if (b.config.question_prompt != null) {
          resourcesRequested.push(b.config.question_prompt)
        }
      }

      if (block.type == 'MobilePrimitives.SelectManyResponse') {
        val b = block as ISelectManyResponseBlock
        if (b.config.prompt != null) {
          resourcesRequested.push(b.config.prompt)
        }
        if (b.config.question_prompt != null) {
          resourcesRequested.push(b.config.question_prompt)
        }
      }

      if (block.type == 'MobilePrimitives.OpenResponse') {
        val b = block as IOpenResponseBlock
        resourcesRequested.push(b.config.prompt)
      }

      if (block.type == 'MobilePrimitives.NumericResponse') {
        val b = block as INumericResponseBlock
        resourcesRequested.push(b.config.prompt)
      }
    })
  })

  val missingResources: List<String> = []
  val allResourceStrings = container.resources.map(r -> r.uuid)

  resourcesRequested.forEach(resourcesString -> {
    if (!allResourceStrings.includes(resourcesString)) {
      missingResources.push(resourcesString)
    }
  })

  if (missingResources.size > 0) {
    return missingResources
  } else {
    return null
  }
}
*/
