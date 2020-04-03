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
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author jorge.correaj
 */
public class ProgramacionNotificarVencimientoCalibracionLEC {
    
    public void crearProgramacion(){
        
        Integer intError=0;
        
        try {
            
            new GIDaoException("Inicio la programación de la tarea NotificarVencimientoCalibracionLEC");
                                        
            intError = 1;
            
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start(); 
            
            // Creación de una instancia de JobDetail.
            JobDetail jobDetail = new JobDetail("NotificarVencimientoCalibracionLECJob", scheduler.DEFAULT_GROUP, NotificarVencimientoCalibracionLEC.class);
            
             intError = 2;
            
            // Se crea el trigger para ejecución el último día del mes a las 07:15 a.m.  
             Trigger trigger = new CronTrigger("NotificarVencimientoCalibracionLECTrigger", Scheduler.DEFAULT_GROUP, "15 07 00 L * ?");
             trigger.setName("tgNotificarVencimientoCalibracionLEC");
             trigger.setGroup("grupoSIU");
             trigger.setPriority(1);
                                                  
             intError = 3;

            // Registro dentro del Scheduler.            
            scheduler.scheduleJob(jobDetail, trigger);
                        
             intError = 4;
             
             new GIDaoException("Finalizó la programación de la tarea NotificarVencimientoCalibracionLEC.");

        } catch(Exception e) {
            new GIDaoException("Se generó un error al ejecutar la tarea NotificarVencimientoCalibracionLEC. Variable error = " +  intError, e);
        }
    } 
}
