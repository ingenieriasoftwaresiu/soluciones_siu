/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.jobs;

import co.edu.udea.solucionessiu.exception.GIDaoException;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author jorge.correaj
 */
public class ProgramacionNotificarVencimientoMntoPretvoEqCi {
    
    public void crearProgramacion(){
        
        Integer intError=0;
        
        try {
            
            new GIDaoException("Inicio la programación de la tarea NotificarVencimientoMntoPretvoEqCi");
                                        
            intError = 1;
            
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start(); 
            
            // Creación de una instancia de JobDetail.
            JobDetail jobDetail = new JobDetail("NotificarVencimientoMntoPretvoEqCiJob", scheduler.DEFAULT_GROUP, NotificarVencimientoMntoPretvoEqCi.class);
            
             intError = 2;
            
            // Se crea el trigger para ejecución el último día del mes a las 07:30 a.m.  
             Trigger trigger = new CronTrigger("NotificarVencimientoMntoPretvoEqCiTrigger", Scheduler.DEFAULT_GROUP, "30 07 00 L * ?");
             trigger.setName("tgNotificarVencimientoMntoPretvoEqCi");
             trigger.setGroup("grupoSIU");
             trigger.setPriority(1);
                                                  
             intError = 3;

            // Registro dentro del Scheduler.            
            scheduler.scheduleJob(jobDetail, trigger);
                        
            intError = 4;
             
             new GIDaoException("Finalizó la programación de la tarea NotificarVencimientoMntoPretvoEqCi.");

        } catch(Exception e) {
            new GIDaoException("Se generó un error al ejecutar la tarea NotificarVencimientoMntoPretvoEqCi. Variable error = " +  intError, e);
        }
    }
}
