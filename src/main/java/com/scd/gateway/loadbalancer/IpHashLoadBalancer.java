package com.scd.gateway.loadbalancer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.RequestDataContext;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;
import org.springframework.util.function.SingletonSupplier;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Slf4j
public class IpHashLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    private final String serviceId;
    private SingletonSupplier<ServiceInstanceListSupplier> serviceInstanceListSingletonSupplier;


    public IpHashLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                              String serviceId) {
        this.serviceInstanceListSingletonSupplier = SingletonSupplier.of(() -> serviceInstanceListSupplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new));
        this.serviceId = serviceId;
        log.info("serviceId: {}", serviceId);
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        HttpHeaders headers = ((RequestDataContext) request.getContext()).getClientRequest().getHeaders();
        String ip = headers.getFirst("X-Real-IP");

        return Objects.requireNonNull(serviceInstanceListSingletonSupplier.obtain())
                .get()
                .next()
                .map(instances -> getInstanceResponse(instances, ip));
    }

    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances, String ip) {
        if (instances.isEmpty()) {
            return new EmptyResponse();
        }

        int hash = ip.hashCode();
        int index = (hash & Integer.MAX_VALUE) % instances.size();
        ServiceInstance instance = instances.get(index);
        log.debug("request instance info: {}", instance.getHost() + ":" + instance.getPort());
        return new DefaultResponse(instance);
    }
}

