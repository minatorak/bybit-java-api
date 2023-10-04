package com.bybit.api.client.service;

import com.bybit.api.client.domain.ProductType;
import com.bybit.api.client.domain.trade.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonConverter {
    private final ObjectMapper mapper = new ObjectMapper();

    public BatchOrderRequest jsonToBatchOrderRequest(String json) throws IOException {
        JsonNode rootNode = mapper.readTree(json);

        String category = rootNode.get("category").asText();

        List<TradeOrderRequest> requestList = new ArrayList<>();

        JsonNode requestArrayNode = rootNode.get("request");
        for (JsonNode requestNode : requestArrayNode) {
            TradeOrderRequest request = TradeOrderRequest.builder()
                    .category(ProductType.valueOf(category.toUpperCase()))
                    .symbol(requestNode.get("symbol").asText())
                    .orderType(TradeOrderType.valueOf(requestNode.get("orderType").asText().toUpperCase()))
                    .transactionSide(TransactionSide.valueOf(requestNode.get("side").asText().toUpperCase()))
                    .qty(requestNode.get("qty").asText())
                    .price(requestNode.has("price") ? requestNode.get("price").asText() : null)
                    .triggerDirection(requestNode.has("triggerDirection") ? requestNode.get("triggerDirection").asInt() : null) // Amend Order ID. Either orderId or orderLinkId is required
                    .orderId(requestNode.has("orderId") ? requestNode.get("orderId").asText() : null) // Amend Order ID. Either orderId or orderLinkId is required
                    .orderLinkId(requestNode.has("orderLinkId") ? requestNode.get("orderLinkId").asText() : null)
                    .orderFilter(requestNode.has("orderFilter") ? requestNode.get("orderFilter").asText() : null)
                    .triggerPrice(requestNode.has("triggerPrice") ? requestNode.get("triggerPrice").asText() : null)
                    .triggerBy(requestNode.has("triggerBy") ?TriggerBy.valueOf(requestNode.get("triggerBy").asText().toUpperCase()) : null)
                    .orderIv(requestNode.has("orderIv") ? requestNode.get("orderIv").asText() : null)
                    .timeInForce(requestNode.has("timeInForce") ? TimeInForce.valueOf(requestNode.get("timeInForce").asText().toUpperCase()) : null)
                    .positionIdx(requestNode.has("positionIdx") ? PositionIdx.valueOf(requestNode.get("positionIdx").asText().toUpperCase()) : null)
                    .orderLinkId(requestNode.has("orderLinkId") ? requestNode.get("orderLinkId").asText() : null)
                    .takeProfit(requestNode.has("takeProfit") ? requestNode.get("takeProfit").asText() : null)
                    .stopLoss(requestNode.has("stopLoss") ? requestNode.get("stopLoss").asText() : null)
                    .tpTriggerBy(requestNode.has("tpTriggerBy") ? TriggerBy.valueOf(requestNode.get("tpTriggerBy").asText().toUpperCase()) : TriggerBy.LAST_PRICE)
                    .slTriggerBy(requestNode.has("slTriggerBy") ? TriggerBy.valueOf(requestNode.get("slTriggerBy").asText().toUpperCase()) : TriggerBy.LAST_PRICE)
                    .reduceOnly(requestNode.has("reduceOnly") && requestNode.get("reduceOnly").asBoolean())
                    .closeOnTrigger(requestNode.has("closeOnTrigger") && requestNode.get("closeOnTrigger").asBoolean())
                    .smpType(requestNode.has("smpType") ? SmpType.valueOf(requestNode.get("smpType").asText().toUpperCase()) : null)
                    .mmp(requestNode.has("mmp") ? requestNode.get("mmp").asBoolean() : null)
                    .tpslMode(requestNode.has("tpslMode") ? requestNode.get("tpslMode").asText() : null)
                    .tpLimitPrice(requestNode.has("tpLimitPrice") ? requestNode.get("tpLimitPrice").asText() : null)
                    .slLimitPrice(requestNode.has("slLimitPrice") ? requestNode.get("slLimitPrice").asText() : null)
                    .tpOrderType(requestNode.has("tpOrderType") ? requestNode.get("tpOrderType").asText() : "Market")
                    .slOrderType(requestNode.has("slOrderType") ? requestNode.get("slOrderType").asText() : "Market")
                    .build();

            requestList.add(request);
        }

        return BatchOrderRequest.builder()
                .category(category)
                .request(requestList)
                .build();
    }

    public BatchOrderRequest convertMapToBatchOrderRequest(Map<String, Object> payload) {
        String category = (String) payload.get("category"); // Required

        List<Map<String, Object>> orderMaps = (List<Map<String, Object>>) payload.get("request");

        List<TradeOrderRequest> orders = new ArrayList<>();
        for (Map<String, Object> orderMap : orderMaps) {
            // Set default value for time in force
            String orderTypeValue = (String) orderMap.get("orderType");
            TradeOrderType currentOrderType = TradeOrderType.valueOf(orderTypeValue.toUpperCase()); // Required
            TimeInForce defaultTimeInForce = (currentOrderType == TradeOrderType.MARKET) ? TimeInForce.IOC : TimeInForce.GTC;

            TradeOrderRequest order = TradeOrderRequest.builder()
                    .category(ProductType.valueOf(category.toUpperCase()))
                    .symbol((String) orderMap.get("symbol"))                          // Required
                    .transactionSide(TransactionSide.valueOf(orderMap.get("side").toString().toUpperCase())) // Required
                    .orderType(currentOrderType)   // Required
                    .qty((String) orderMap.get("qty"))                                // Required
                    .orderId((String) orderMap.getOrDefault("orderId", null))              // Amend Order ID. Either orderId or orderLinkId is required
                    .orderLinkId((String) orderMap.getOrDefault("orderLinkId", null))              // Amend Order ID. Either orderId or orderLinkId is required
                    .triggerDirection((Integer) orderMap.getOrDefault("triggerDirection", null)) // Optional
                    .orderFilter((String) orderMap.getOrDefault("orderFilter", null))  // Optional
                    .triggerPrice((String) orderMap.getOrDefault("triggerPrice", null)) // Optional
                    .triggerBy(orderMap.containsKey("triggerBy") ? TriggerBy.valueOf(orderMap.get("triggerBy").toString().toUpperCase()) : null) // Optional
                    .orderIv((String) orderMap.getOrDefault("orderIv", null))        // Optional
                    .timeInForce(TimeInForce.valueOf(orderMap.getOrDefault("timeInForce", defaultTimeInForce).toString().toUpperCase())) // Optional and default value depends on order type
                    .positionIdx(orderMap.containsKey("positionIdx") ? getOrderPositionIndex(orderMap.get("positionIdx").toString()) : null) // Optional
                    .orderLinkId((String) orderMap.getOrDefault("orderLinkId", null)) // Optional
                    .takeProfit((String) orderMap.getOrDefault("takeProfit", null))  // Optional
                    .stopLoss((String) orderMap.getOrDefault("stopLoss", null))      // Optional
                    .tpTriggerBy(orderMap.containsKey("tpTriggerBy") ? TriggerBy.valueOf(orderMap.get("tpTriggerBy").toString().toUpperCase()) : TriggerBy.LAST_PRICE) // Optional, default to LastPrice
                    .slTriggerBy(orderMap.containsKey("slTriggerBy") ? TriggerBy.valueOf(orderMap.get("slTriggerBy").toString().toUpperCase()) : TriggerBy.LAST_PRICE) // Optional, default to LastPrice
                    .reduceOnly((Boolean) orderMap.getOrDefault("reduceOnly", false))  // Optional, default to false
                    .closeOnTrigger((Boolean) orderMap.getOrDefault("closeOnTrigger", false))  // Optional, default to false
                    .smpType(orderMap.containsKey("smpType") ? SmpType.valueOf(orderMap.get("smpType").toString().toUpperCase()) : null) // Optional, replace DEFAULT_SMP_TYPE_VALUE with a real default if needed
                    .mmp((Boolean) orderMap.getOrDefault("mmp", null))               // Optional, default to false
                    .tpslMode((String) orderMap.getOrDefault("tpslMode", null))      // Optional
                    .tpLimitPrice((String) orderMap.getOrDefault("tpLimitPrice", null)) // Optional
                    .slLimitPrice((String) orderMap.getOrDefault("slLimitPrice", null)) // Optional
                    .tpOrderType((String) orderMap.getOrDefault("tpOrderType", "null"))  // Optional, default to Market
                    .slOrderType((String) orderMap.getOrDefault("slOrderType", "null"))  // Optional, default to Market
                    .build();

            orders.add(order);
        }

        return BatchOrderRequest.builder()
                .category(category)
                .request(orders)
                .build();
    }

    public PositionIdx getOrderPositionIndex(String positionIdxValue) {
        int index = Integer.parseInt(positionIdxValue);
        for (PositionIdx positionIdx : PositionIdx.values()) {
            if (positionIdx.getIndex() == index) {
                return positionIdx;
            }
        }
        return null;
    }
}