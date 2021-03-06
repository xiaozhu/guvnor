/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.client.decisiontable.widget;

import org.drools.guvnor.client.modeldriven.ui.RuleAttributeWidget;
import org.drools.guvnor.client.widgets.decoratedgrid.AbstractCellValueFactory;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt.ActionInsertFactCol;
import org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.ide.common.client.modeldriven.dt.RowNumberCol;

/**
 * A Factory to create CellValues applicable to given columns.
 */
public class DecisionTableCellValueFactory extends AbstractCellValueFactory<DTColumnConfig> {

    // Model used to determine data-types etc for cells
    private GuidedDecisionTable model;

    /**
     * Construct a Cell Value Factory for a specific Decision Table
     * 
     * @param sce
     *            SuggestionCompletionEngine to assist with drop-downs
     * @param model
     *            The Decision Table model to assist data-type derivation
     */
    public DecisionTableCellValueFactory(SuggestionCompletionEngine sce,
                                         GuidedDecisionTable model) {
        super( sce );
        if ( model == null ) {
            throw new IllegalArgumentException( "model cannot be null" );
        }
        this.model = model;
    }

    // Get the Data Type corresponding to a given column
    protected DATA_TYPES getDataType(DTColumnConfig column) {

        DATA_TYPES dataType = DATA_TYPES.STRING;

        if ( column instanceof RowNumberCol ) {
            dataType = DATA_TYPES.ROW_NUMBER;

        } else if ( column instanceof AttributeCol ) {
            AttributeCol attrCol = (AttributeCol) column;
            String attrName = attrCol.getAttribute();
            if ( attrName.equals( RuleAttributeWidget.SALIENCE_ATTR ) ) {
                if ( attrCol.isUseRowNumber() ) {
                    dataType = DATA_TYPES.ROW_NUMBER;
                } else {
                    dataType = DATA_TYPES.NUMERIC;
                }
            } else if ( attrName.equals( RuleAttributeWidget.ENABLED_ATTR ) ) {
                dataType = DATA_TYPES.BOOLEAN;
            } else if ( attrName.equals( RuleAttributeWidget.NO_LOOP_ATTR ) ) {
                dataType = DATA_TYPES.BOOLEAN;
            } else if ( attrName.equals( RuleAttributeWidget.DURATION_ATTR ) ) {
                dataType = DATA_TYPES.NUMERIC;
            } else if ( attrName.equals( RuleAttributeWidget.AUTO_FOCUS_ATTR ) ) {
                dataType = DATA_TYPES.BOOLEAN;
            } else if ( attrName.equals( RuleAttributeWidget.LOCK_ON_ACTIVE_ATTR ) ) {
                dataType = DATA_TYPES.BOOLEAN;
            } else if ( attrName.equals( RuleAttributeWidget.DATE_EFFECTIVE_ATTR ) ) {
                dataType = DATA_TYPES.DATE;
            } else if ( attrName.equals( RuleAttributeWidget.DATE_EXPIRES_ATTR ) ) {
                dataType = DATA_TYPES.DATE;
            } else if ( attrName.equals( RuleAttributeWidget.DIALECT_ATTR ) ) {
                dataType = DATA_TYPES.DIALECT;
            }

        } else if ( column instanceof ConditionCol ) {
            dataType = makeNewCellDataType( column );

        } else if ( column instanceof ActionSetFieldCol ) {
            dataType = makeNewCellDataType( column );

        } else if ( column instanceof ActionInsertFactCol ) {
            dataType = makeNewCellDataType( column );

        }

        return dataType;

    }

    // Derive the Data Type for a Condition or Action column
    private DATA_TYPES makeNewCellDataType(DTColumnConfig col) {

        DATA_TYPES dataType = DATA_TYPES.STRING;

        // Columns with lists of values, enums etc are always Text (for now)
        String[] vals = model.getValueList( col,
                                            sce );
        if ( vals.length == 0 ) {
            if ( model.isNumeric( col,
                                  sce ) ) {
                dataType = DATA_TYPES.NUMERIC;
            } else if ( model.isBoolean( col,
                                         sce ) ) {
                dataType = DATA_TYPES.BOOLEAN;
            } else if ( model.isDate( col,
                                      sce ) ) {
                dataType = DATA_TYPES.DATE;
            }
        }
        return dataType;
    }

}
