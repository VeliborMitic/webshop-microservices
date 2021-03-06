package order.service.persistence;

import order.service.mappers.OrderMapper;
import order.service.services.OrderDTO;
import order.service.services.feign.OrderOut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderProviderImpl implements OrderProvider {

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    public OrderProviderImpl(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public Page<OrderOut> getAll(Pageable pageable) {
        return orderRepository.findAllByDateDescSorted(pageable)
                .map(orderMapper::toOrderOut);
    }

    @Override
    public OrderDTO getById(Long id) {
        return orderMapper.toDTO(
                orderRepository.findById(id)
                        .orElseThrow(IllegalAccessError::new)
        );
    }

    @Override
    public OrderDTO save(OrderDTO orderDTO) {
        return orderMapper.toDTO(
                orderRepository.save(
                        orderMapper.toModel(orderDTO)
                )
        );
    }

    @Override
    public void deleteById(Long id) {
        if (!orderRepository.existsById(id))
            throw new IllegalArgumentException();

        orderRepository.deleteById(id);
    }

    @Override
    public void deleteProductFromOrders(Long productId) {
        List<Order> orders = orderRepository.findAllByProductIdsIsContaining(productId);

        if (!orders.isEmpty()) {
            orders.forEach(order -> order.getProductIds().removeIf(e -> e.equals(productId)));
            orderRepository.saveAll(
                    orders.stream()
                            .filter(OrderProviderImpl::isNotEmpty)
                            .collect(Collectors.toList())
            );
        }
    }

    @Override
    public Page<OrderOut> getByUser(String name, Pageable pageable) {
        return orderRepository.findAllByUserId(name, pageable)
                .map(orderMapper::toOrderOut);
    }

    private static boolean isNotEmpty(Order e) {
        return !e.getProductIds().isEmpty();
    }

}
