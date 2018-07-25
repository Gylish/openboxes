/**
* Copyright (c) 2012 Partners In Health.  All rights reserved.
* The use and distribution terms for this software are covered by the
* Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
* which can be found in the file epl-v10.html at the root of this distribution.
* By using this software in any fashion, you are agreeing to be bound by
* the terms of this license.
* You must not remove this notice, or any other, from this software.
**/ 

package org.pih.warehouse.inventory

import grails.converters.JSON
import org.pih.warehouse.api.StockMovement
import org.pih.warehouse.importer.ImportDataCommand


class StockMovementController {

    def dataService
    def stockMovementService

	def index = {
		render(template: "/stockMovement/create")
	}


	def exportCsv = {
        StockMovement stockMovement = stockMovementService.getStockMovement(params.id)
        def lineItems = stockMovement.lineItems.collect {
            [
                    requisitionItemId: it.id,
                    productId: it.product.id,
                    productCode: it.product.productCode,
                    productName: it.product.name,
                    palletName: it.palletName,
                    boxName: it.boxName,
                    quantity: it.quantityRequested,
                    recipientId: it?.recipient?.id
            ]
        }
        String csv = dataService.generateCsv(lineItems)
        response.setHeader("Content-disposition", "attachment; filename='StockMovementItems-${params.id}.csv'")
        render(contentType:"text/csv", text: csv.toString(), encoding:"UTF-8")
	}


	def importCsv = { ImportDataCommand command ->
        StockMovement stockMovement = stockMovementService.getStockMovement(params.id)

        def file = command.importFile
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty")
        }

        render([data: "Data will be imported successfully"] as JSON)
	}

}